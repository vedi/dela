package dela

import dela.context.DataContext

class SubjectService extends DataService<Subject> {

    static transactional = true

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
        owner.removeFromSubjects(subject)
        dataContext.setup.removeFromFilterSubjects(subject)
        if (subject.equals(dataContext.setup.activeSubject)) {
            dataContext.setup.activeSubject = null
        }
        dataContext.setup = dataContext.setup.merge()

        result

    }


}
