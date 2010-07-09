package dela.ui.task

import com.vaadin.data.Item
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Component
import com.vaadin.ui.Field
import com.vaadin.ui.Form
import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.Slider
import com.vaadin.ui.Table.TableDragMode
import com.vaadin.ui.TextField
import dela.Setup
import dela.State
import dela.Subject
import dela.Task
import dela.meta.MetaProvider
import dela.ui.common.EntityTable

/**
 * @author vedi
 * date 04.07.2010
 * time 20:29:22
 */
public class TaskTable extends EntityTable implements FormFieldFactory {

    def storeService
    Button completeButton


    def selector = {startIndex, count, sortProperty, ascendingState ->
        if (sortProperty) {
            throw new UnsupportedOperationException()
        }

        Setup setup = storeService.setup
        Task.findAllByStateInListAndSubjectInList(setup.filterStates, setup.filterSubjects, [offset:startIndex,  max:count, sort:'power', order:'desc'])
    }

    def counter = {
        Setup setup = storeService.setup
        Task.countByStateInListAndSubjectInList(setup.filterStates, setup.filterSubjects)
    }

    def gridVisibleColumns = ['subject', 'name']
    def editVisibleColumns = ['subject', 'name', 'description', 'state', 'power']

    def formFieldFactory = this

    def MetaProvider metaProvider

    def TaskTable() {
        storeService = getBean(dela.StoreService.class)
    }

    protected Form createForm() {
        return new TaskForm()
    }

    def Object createDomain() {
        Task task = new Task()
        task.subject = storeService.setup.activeSubject
        task.state = State.findAll()[0]  // FIXME: Жёсткая привязка к id состояния

        def firstItemId = container.firstItemId();
        double firstPower = firstItemId != null ? container.getItem(firstItemId)?.getItemProperty('power')?.value?:0.0 as double : 0.0;
        task.power = (1.0 + firstPower) / 2;
        return task
    }

    def initGrid() {
        this.table.setColumnWidth 'subject', 80
        this.table.setDragMode(TableDragMode.ROW)
    }

    protected void initToolBar() {
        super.initToolBar()

        toolBarLayout.addComponent(completeButton = new Button('complete', this))
    }

    def void buttonClick(ClickEvent clickEvent) {
        if (clickEvent.button == completeButton) {
            def item = container.getItem(table.value)
            if (item) {
                // TODO: Move to service
                Task.withTransaction {
                    State state = State.get(2) // FIXME: Hardcoded state id
                    assert state

                    Task task = Task.get(item.getItemProperty('id').value as Long)
                    assert task
                    if (!state.equals(task.state)) {
                        task.state = state;
                        task.merge()
                        this.refresh()
                    }
                }
            }
        } else {
            super.buttonClick(clickEvent);
        }
    }



    Field createField(Item item, Object propertyId, Component component) {
        String label = metaDomain.getMetaColumn(propertyId)?.label?:propertyId
        if (propertyId.equals("subject")) {
            def comboBox = new ComboBox(caption:label, immediate: true)
            Subject.findAll().each {
                comboBox.addItem it
            }

            comboBox
        } else if (propertyId.equals('state')) {
            def comboBox = new ComboBox(caption:label, immediate: true)
            State.findAll().each {
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
