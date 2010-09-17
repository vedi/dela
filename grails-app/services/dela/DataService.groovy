package dela

import dela.context.DataContext

/**
 * canInsert, canEdit - is action available in current context, runs twice or more times in UI and in logic
 * canSave - can the domain be persisted, for edit action ititial state gives
 * domain's validate - last validation 
 */
class DataService<T> implements IDataService<T> {

    def anonymous
    
    def create(DataContext dataContext) {
        return dataContext.metaDomain.domainClass.newInstance()
    }

    def save(DataContext dataContext, T domain) {

        boolean isNew = domain.id == null

        def gainedDomain = isNew?null:gainDomain(dataContext, domain)

        if (isNew) {
            assert canInsert(dataContext)
        } else {
            assert canEdit(dataContext, gainedDomain)
        }

        assert canSave(dataContext, gainedDomain, domain)

        domain = domain.merge()

        if (isNew) {
            afterInsert(domain)
        } else {
            afterEdit(domain)
        }

        return domain
    }

    def delete(DataContext dataContext, T domain) {
        assert canDelete(dataContext, gainDomain(dataContext, domain))
        domain.delete()
    }

    def afterInsert(T domain) {
    }

    def afterEdit(T domain) {
    }

    def Boolean canInsert(DataContext dataContext) {
        return true
    }

    def Boolean canEdit(DataContext dataContext, T domain) {
        return true
    }

    def Boolean canDelete(DataContext dataContext, T domain) {
        return canEdit(dataContext, domain)
    }

    def Boolean canSave(DataContext dataContext, T oldDomain, T newDomain) {
        return true
    }

    protected T gainDomain(DataContext dataContext, T domain) {
        dataContext.metaDomain.domainClass.get(domain.id) //TODO: Test the same
    }
}
