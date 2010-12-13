package dela.ui.common

import com.vaadin.data.Item
import com.vaadin.ui.Component
import com.vaadin.ui.DefaultFieldFactory
import com.vaadin.ui.Field
import com.vaadin.ui.TextField

/**
 * @author vedi
 * date 13.12.10
 * time 8:37
 */
class SimpleEntityForm extends EntityForm {

    def fineNullRepresentation = {field ->
        if (field instanceof TextField) {
            ((TextField)field).setNullRepresentation('')
        }

        field
    }

    def converters = [fineNullRepresentation]

    class FormFieldFactory extends DefaultFieldFactory {
        @Override
        Field createField(Item item, Object propertyId, Component uiContext) {
            Field field = super.createField(item, propertyId, uiContext)

            converters.each {field = it(field)}

            field
        }
    }

    SimpleEntityForm() {
        this.formFieldFactory = new FormFieldFactory()
    }
}