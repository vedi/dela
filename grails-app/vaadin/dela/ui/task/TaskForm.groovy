package dela.ui.task

import com.vaadin.ui.Field
import com.vaadin.ui.FormLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.VerticalLayout
import dela.ui.common.EntityForm

/**
 * @author vedi
 * date 04.07.2010
 * time 20:23:30
 */
class TaskForm extends EntityForm {

    def leftLayout
    def rightLayout

    def TaskForm() {
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

    def void attach() {
        super.attach()

        leftLayout.components[1].focus()
    }



    protected void attachField(Object propertyId, Field field) {
        if (propertyId.equals("power")) {
            rightLayout.addComponent(field)
        } else {
            leftLayout.addComponent(field)
        }
    }


}
