package dela

import com.vaadin.data.Item
import com.vaadin.ui.Component
import com.vaadin.ui.Field
import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.TextField
import dela.container.DomainMeta

/**
 * @author vedi
 * date 28.06.2010
 * time 15:13:41
 */

class SubjectDvUtils {
    public static DomainMeta DEFAULT_DV = new DomainMeta(
            domainClass: Subject.class,
            caption: "Subjects",
            columns: [
                    new DomainMeta.ColumnDefinition(field: 'name', readOnly: true, sortable: true),
                    new DomainMeta.ColumnDefinition(field: 'description'),
            ],
            gridVisible: ['name'],
    )

    public static FormFieldFactory DEFAULT_FFF = new FormFieldFactory() {

        @Override
        Field createField(Item item, Object propertyId, Component component) {
            String label = DEFAULT_DV.getDef(propertyId)?.label?:propertyId
            TextField textField = new TextField(label)
            textField.setNullRepresentation('')

            if ('description'.equals(propertyId)) {
                textField.setRows(10)
                textField.setColumns(30)
            }

            textField
        }
    }
}