package dela.ui.subject

import dela.ui.common.EntityForm
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button
import com.vaadin.ui.ComponentContainer
import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.Field
import com.vaadin.data.Item
import com.vaadin.ui.Component
import com.vaadin.ui.CheckBox
import com.vaadin.ui.TextField

/**
 * @author vedi
 * date 29.11.10
 * time 21:34
 */
class SubjectForm extends EntityForm implements FormFieldFactory {

    def taskService
    def messageService

    def normalizeButton

    SubjectForm() {
        this.taskService = getBean(dela.TaskService.class)
        this.messageService = getBean(dela.MessageService.class)

        this.formFieldFactory = this
    }

    protected void initButtons(ComponentContainer componentContainer) {
        super.initButtons(componentContainer);

        if (editable) {
            normalizeButton = new Button()
            normalizeButton.caption = messageService.getNormalizeButtonLabel()
            normalizeButton.addListener(this as Button.ClickListener)
            componentContainer.addComponent(normalizeButton)
        }
    }

    def void buttonClick(ClickEvent clickEvent) {
        if (clickEvent.button == normalizeButton) {
            taskService.normalizeSubjectTasks(getDomain(getItemDataSource()))
            window.application.mainWindow.showNotification(messageService.getNormalizeIsCompletedMsg())
        } else {
            super.buttonClick(clickEvent)
        }
    }

    Field createField(Item item, Object propertyId, Component component) {
        String label = getColumnLabel(propertyId)
        if ('isPublic'.equals(propertyId)) {
            def checkBox = new CheckBox(label)
            checkBox.readOnly = !this.dataContext.account.isAdmin()
            checkBox
        } else {
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
