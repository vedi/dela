package dela.ui.task

import com.vaadin.ui.Field
import com.vaadin.ui.FormLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.VerticalLayout
import dela.ui.common.EntityForm
import com.vaadin.ui.TextField
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Slider
import com.vaadin.data.Item
import com.vaadin.ui.Component
import com.vaadin.ui.FormFieldFactory

/**
 * @author vedi
 * date 04.07.2010
 * time 20:23:30
 */
class TaskForm extends EntityForm implements FormFieldFactory {

    def leftLayout
    def rightLayout

    def TaskForm() {
        this.formFieldFactory = this

        def mainLayout = new HorizontalLayout()

        leftLayout = new FormLayout()
        mainLayout.addComponent(leftLayout)

        def separator = new VerticalLayout()
        separator.setWidth '32px'
        mainLayout.addComponent(separator)

        rightLayout = new VerticalLayout()
        rightLayout.setWidth '48px'
        rightLayout.setHeight '100%'
        mainLayout.addComponent(rightLayout)

        this.layout = mainLayout
    }

    protected void attachField(Object propertyId, Field field) {
        if (propertyId.equals("power")) {
            rightLayout.addComponent(field)
        } else {
            leftLayout.addComponent(field)
        }
    }

    @Override
    protected List<String> getEditVisibleColumns() {
        ['name', 'description', 'subject', 'state', 'power']
    }

    Field createField(Item item, Object propertyId, Component component) {
        String caption = getColumnLabel(propertyId)
        if (propertyId.equals("subject")) {
            def comboBox = new ComboBox(caption:caption, immediate: true)
            dataContext.storeService.getSubjects().each {
                comboBox.addItem it
            }

            comboBox
        } else if (propertyId.equals('state')) {
            def comboBox = new ComboBox(caption:caption, immediate: true)
            dataContext.storeService.getStates().each {
                comboBox.addItem it
            }

            comboBox
        } else if (propertyId.equals('power')) {
            Slider slider = new Slider(caption:caption,
                    min: 0.01, max: 0.99, resolution: 2, orientation: Slider.ORIENTATION_VERTICAL,
                    immediate: true)

            slider.setHeight "100%"

            slider
        } else {
            def textField = new TextField(caption)
            textField.setNullRepresentation('')

            if ('description'.equals(propertyId)) {
                textField.setRows(10)
                textField.setColumns(30)
            } else if ('name'.equals(propertyId)) {
                defaultComponent = textField
                textField.setColumns(30)
            }

            textField
        }
    }

}
