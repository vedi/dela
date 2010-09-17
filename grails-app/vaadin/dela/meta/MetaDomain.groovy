package dela.meta

/**
 * @author vedi
 * date 08.07.2010
 * time 20:33:25
 */
public class MetaDomain {

    Class domainClass

    String caption

    private Map<String, MetaColumn> innerColumns = [:]

    def save = {domain ->
        assert domain.save(), domain.errors
    } 

    def getColumns() {
        innerColumns.collect {it.value}
    }

    def setColumns(columns) {
        columns.each {
            innerColumns[it.field] = it
        }
    }

    def getMetaColumn(field) {
        innerColumns[field]
    }
}
