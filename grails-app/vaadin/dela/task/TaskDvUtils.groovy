package dela.task

import com.vaadin.data.Item
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Component
import com.vaadin.ui.Field
import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.Slider
import com.vaadin.ui.TextField
import dela.Setup
import dela.State
import dela.StateDvUtils
import dela.Subject
import dela.SubjectDvUtils
import dela.Task
import dela.Version
import dela.container.DomainMeta

/**
 * @author vedi
 * date 28.06.2010
 * time 15:13:41
 */
class TaskDvUtils {

    def storeService

    public final DomainMeta defaultDv = new DomainMeta(
            domainClass: Task.class,
            caption: "Tasks",
            columns: [
                    new DomainMeta.ColumnDefinition(field: 'id', type:Long.class, readOnly: true),
                    new DomainMeta.ColumnDefinition(field: 'name', readOnly: true),
                    new DomainMeta.ColumnDefinition(field: 'description'),
                    new DomainMeta.ColumnDefinition(field: 'power', type:Double.class, ),
                    new DomainMeta.ColumnDefinition(field: 'subject', type:Subject.class, width: 80),
                    new DomainMeta.ColumnDefinition(field: 'state', type:State.class),
                    new DomainMeta.ColumnDefinition(field: 'subjectVersion',type:Version.class, label: 'version'),
                    new DomainMeta.ColumnDefinition(field: 'dateCreated', type:Date.class, label:'creation date'),
            ],
            selector: {startIndex, count, sortProperty, ascendingState ->
                if (sortProperty) {
                    throw new UnsupportedOperationException()
                }

                Setup setup = storeService.setup
                Task.findAllByStateInListAndSubjectInList(setup.filterStates, setup.filterSubjects, [offset:startIndex,  max:count, sort:'power', order:'desc'])
            },
            counter: {
                Setup setup = storeService.setup
                Task.countByStateInListAndSubjectInList(setup.filterStates, setup.filterSubjects)
            },
            gridVisible: ['subject', 'name'],
            editVisible: ['subject', 'name', 'description', 'state', 'power']
    )

    public final FormFieldFactory defaultFff = new FormFieldFactory() {

        Field createField(Item item, Object propertyId, Component component) {
            String label = defaultDv.getDef(propertyId)?.label?:propertyId
            if (propertyId.equals("subject")) {
                def comboBox = new ComboBox(caption:label, immediate: true)
                SubjectDvUtils.DEFAULT_DV.selector(0, 100, null, null).each {
                    comboBox.addItem it
                }

                comboBox
            } else if (propertyId.equals('state')) {
                def comboBox = new ComboBox(caption:label, immediate: true)
                StateDvUtils.DEFAULT_DV.selector(0, 100, null, null).each {
                    comboBox.addItem it
                }

                comboBox
            } else if (propertyId.equals('power')) {
                Slider slider = new Slider(caption:'task power',
                        min: 0.0, max: 1.0, resolution: 2, orientation: Slider.ORIENTATION_VERTICAL,
                        immediate: true)

                slider.setHeight("100px")

                slider
            } else {
                def textField = new TextField(label)
                textField.setNullRepresentation('')

                if ('description'.equals(propertyId)) {
                    textField.setRows(10)
                    textField.setColumns(30)
                } else if ('name'.equals(propertyId)) {
                    textField.setColumns(30)
                }

                textField
            }
        }
    }
}
