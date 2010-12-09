package dela

import dela.context.DataContext

/**
 * canInsert, canEdit - is action available in current context, runs twice or more times in UI and in logic
 * canSave - can the domain be persisted, for edit action initial state gives
 * domain's validate - last validation 
 */
abstract class DataService<T> implements IDataService<T> {

    static DEFAULT_DATA_VIEW = 'default'

    Class<T> domainClass

    DataService(Class<T> domainClass) {
        this.domainClass = domainClass
    }

    def getDataViewFactories() {
        [(DEFAULT_DATA_VIEW): {dataContext ->
            new DataView(
                    counter: {
                        dataContext.domainClass.count()
                    },
                    selector: {startIndex, count, sortProperty, ascendingState ->
                        if (sortProperty) {
                            dataContext.domainClass.list(offset:startIndex,  max:count, sort:sortProperty, order:ascendingState)
                        } else {
                            dataContext.domainClass.list(offset:startIndex,  max:count)
                        }
                    }
            )
        }]
    }

    @Override
    final DataView getDataView(dataContext) {
        def factory = dataViewFactories[dataContext.dataViewName]
        return factory ? factory(dataContext) : null
    }

    @Override
    def create(DataContext dataContext) {
        return dataContext.domainClass.newInstance()
    }

    @Override
    def save(DataContext dataContext, T domain) {

        domain.withTransaction {
            boolean isNew = domain.id == null

            def gainedDomain = isNew?null:gainDomain(dataContext, domain)

            if (isNew) {
                assert canInsert(dataContext)
            } else {
                assert canEdit(dataContext, gainedDomain)
            }

            // TODO: Translate to VAADIN validation
            assert canSave(dataContext, gainedDomain, domain)

            if (isNew) {
                assert domain.save()
                afterInsert(dataContext, domain)
            } else {
                assert domain.validate()
                domain = domain.merge()
                afterEdit(dataContext, domain)
            }

            return domain
        }
    }

    @Override
    def delete(DataContext dataContext, T domain) {
        domain.withTransaction {
            assert canDelete(dataContext, gainDomain(dataContext, domain))
            domain.merge().delete()
            afterDelete(dataContext, domain)
        }
    }

    @Override
    def afterInsert(DataContext dataContext, T domain) {
    }

    @Override
    def afterEdit(DataContext dataContext, T domain) {
    }

    @Override
    def afterDelete(DataContext dataContext, T domain) {
    }

    @Override
    def Boolean canInsert(DataContext dataContext) {
        return true
    }

    @Override
    def Boolean canEdit(DataContext dataContext, T domain) {
        return true
    }

    @Override
    def Boolean canDelete(DataContext dataContext, T domain) {
        return canEdit(dataContext, domain)
    }

    @Override
    def Boolean canSave(DataContext dataContext, T oldDomain, T newDomain) {
        return true
    }

    @Override
    def createDataContext(sessionContext, dataViewName = DataService.DEFAULT_DATA_VIEW) {
        new DataContext(sessionContext: sessionContext,
                dataService: this, domainClass: domainClass, dataViewName: dataViewName)
    }

    protected T gainDomain(DataContext dataContext, T domain) {
        dataContext.domainClass.get(domain.id) //TODO: Test the same
    }
}
