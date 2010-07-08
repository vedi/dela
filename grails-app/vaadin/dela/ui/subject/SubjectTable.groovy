package dela.ui.subject

import com.vaadin.data.Item
import com.vaadin.ui.Component
import com.vaadin.ui.Field
import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.TextField
import dela.ui.common.EntityTable

/**
 * @author vedi
 * date 08.07.2010
 * time 22:33:52
 */
class SubjectTable extends EntityTable implements FormFieldFactory {

    def gridVisibleColumns = ['name']
    def formFieldFactory = this

    Field createField(Item item, Object propertyId, Component component) {
        String label = metaDomain.getMetaColumn(propertyId)?.label?:propertyId
        TextField textField = new TextField(label)
        textField.setNullRepresentation('')

        if ('description'.equals(propertyId)) {
            textField.setRows(10)
            textField.setColumns(30)
        }

        textField
    }
}
