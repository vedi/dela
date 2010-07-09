package dela.ui.subject

import com.vaadin.data.Item
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Component
import com.vaadin.ui.Field
import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.TextField
import dela.DataService
import dela.ui.common.EntityForm
import dela.ui.common.EntityTable

/**
 * @author vedi
 * date 08.07.2010
 * time 22:33:52
 */
class SubjectTable extends EntityTable implements FormFieldFactory {

    DataService dataService

    def gridVisibleColumns = ['name']
    def formFieldFactory = this
    def normalizeButton

    def SubjectTable() {
        dataService = getBean(DataService.class)
    }

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

    protected EntityForm createForm() {
        return new SubjectForm()
    }

    class SubjectForm extends EntityForm {

        protected void initButtons() {
            super.initButtons();

            getFooter().addComponent(normalizeButton = new Button("normalize", this as Button.ClickListener))
        }

        def void buttonClick(ClickEvent clickEvent) {
            if (clickEvent.button == normalizeButton) {
                Long subjectId = getItemDataSource().getItemProperty('id').value as Long

                assert subjectId

                dataService.normalizeSubject(subjectId)

                window.application.mainWindow.showNotification("Normalize is completed")
            } else {
                super.buttonClick(clickEvent)
            }
        }


    }

}
