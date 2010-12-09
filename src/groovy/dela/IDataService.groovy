package dela

import dela.context.DataContext
import dela.meta.MetaColumn

/**
 * @author vedi
 * date 13.09.2010
 * time 9:13:00
 */
public interface IDataService<T> {

    def getColumns()

    DataView getDataView(dataContext)

    T save(DataContext dataContext, T domain)

    T create(DataContext dataContext)

    def delete(DataContext dataContext, T domain)

    def afterInsert(DataContext dataContext, T domain)

    def afterEdit(DataContext dataContext, T domain)

    def afterDelete(DataContext dataContext, T domain)

    def Boolean canInsert(DataContext dataContext)

    def Boolean canEdit(DataContext dataContext, T domain)

    def Boolean canDelete(DataContext dataContext, T domain)

    /**
     * @param oldDomain original domain or null in inserting
     * @param newDomain domain with new values
     * @return can or can't )
     */
    def Boolean canSave(DataContext dataContext, T oldDomain, T newDomain)

    def createDataContext(sessionContext, dataViewName)
}