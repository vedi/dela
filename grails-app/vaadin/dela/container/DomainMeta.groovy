package dela.container

/**
 * @author vedi
 * date 28.06.2010
 * time 14:43:36
 */
public class DomainMeta {

    public static class ColumnDefinition {
        String field
        Class type = String.class
        def defaultValue
        boolean readOnly = false
        boolean sortable = false
        String label
        def width
    }

    Class domainClass

    def selector = {startIndex, count, sortProperty, ascendingState ->
        if (sortProperty) {
            domainClass.list(offset:startIndex,  max:count, sort:sortProperty, order:ascendingState)
        } else {
            domainClass.list(offset:startIndex,  max:count)
        }
    }

    def counter = {
        domainClass.count()
    }

    String caption

    private Map<String, ColumnDefinition> innerColumns = [:]

    def getColumns() {
        innerColumns.collect {it.value}
    }

    def setColumns(columns) {
        columns.each {
            innerColumns[it.field] = it
        }
    }

    def getShortColumns() {
        [innerColumns['id'], innerColumns[nameField?:'name']]
    }

    def nameField

    def gridVisible
    def editVisible

    def getGridVisible() {
        gridVisible?:
            (innerColumns.collect {
                it.value.field
            })
    }

    def getEditVisible() {
        editVisible?:
            (innerColumns.collect {
                it.value.field
            })
    }

    def getColumnWidth(column) {
        innerColumns[column]?.width?:-1
    }

    def getDef(field) {
        innerColumns[field]
    }
}