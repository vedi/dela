package dela

import dela.context.DataContext
import dela.meta.MetaColumn

class SubjectService extends DataService<Subject> {

    static OWN_AND_PUBLIC_DATA_VIEW = 'ownAndPublicDataView'

    def columns = [
            new MetaColumn(field: 'name'),
            new MetaColumn(field: 'description'),
            new MetaColumn(field: 'isPublic'),
            new MetaColumn(field: 'owner')
    ]

    def dataViewFactories = [(OWN_AND_PUBLIC_DATA_VIEW): {dataContext ->
            new DataView(
                    selector: {startIndex, count, sortProperty, ascendingState ->
                        Subject.findAllByOwnerOrIsPublic(dataContext.account, true, [offset:startIndex,  max:count, sort:sortProperty, order:ascendingState])
                    },
                    counter: {
                        Subject.countByOwnerOrIsPublic(dataContext.account, true)
                    }
            )
        }]

    static transactional = true

    def messageService

    SubjectService() {
        super(Subject)
    }

    def Subject create(DataContext dataContext) {
        return new Subject(owner: dataContext.account as Account)
    }

    def Subject save(DataContext dataContext, Subject subject) {

        super.save(dataContext, subject)

        Setup setup = dataContext.setup as Setup
        setup.addToFilterSubjects(subject)

        subject
    }

    def Boolean canInsert(DataContext dataContext) {
        return dataContext.account.isNotAnonymous()
    }

    def Boolean canEdit(DataContext dataContext, Subject subject) {
        return dataContext.account.isAdmin() ||
                (dataContext.account.isNotAnonymous() &&
                        dataContext.account.equals(subject.owner))
    }

    def Boolean canSave(DataContext dataContext, Subject oldDomain, Subject newDomain) {
        if (oldDomain == null) {
            return dataContext.account.isNotAnonymous() && (dataContext.account.isAdmin() ||
                    (!newDomain.isPublic && newDomain.owner == dataContext.account))
        } else {
            return dataContext.account.isAdmin() ||
                    (dataContext.account.isNotAnonymous() && dataContext.account == oldDomain.owner && oldDomain.owner == newDomain.owner) &&
                            oldDomain.isPublic == newDomain.isPublic
        }
    }

    def afterDelete(DataContext dataContext, Subject subject) {
        def result = super.afterDelete(dataContext, subject);

        def owner = subject.owner

        //TODO: Удаляется только из настроек сессии!
        dataContext.account.removeFromSubjects(subject)

        owner.removeFromSubjects(subject)
        def setup = dataContext.setup
        setup.removeFromFilterSubjects(subject)
        if (subject == setup.activeSubject) {
            setup.activeSubject = null
        }
        dataContext.storeService.saveSetup(setup)

        result

    }

    /**
     * @param account owner of a created subject
     * @return new subject
     */
    def createDefault(Account account) {
        def subject = new Subject(owner: account,
                name: messageService.getMessage('default.subject.name'),
                description: messageService.getMessage('default.subject.description'),
                isPublic: false)

        account.addToSubjects(subject) // TODO: Remove?!

        subject
    }

}
