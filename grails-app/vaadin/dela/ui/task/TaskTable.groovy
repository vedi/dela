package dela.ui.task

import com.vaadin.data.Container
import com.vaadin.data.Container.Ordered
import com.vaadin.data.Item
import com.vaadin.event.ShortcutAction
import com.vaadin.event.ShortcutListener
import com.vaadin.event.dd.DragAndDropEvent
import com.vaadin.event.dd.DropHandler
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion
import com.vaadin.terminal.FileResource
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation
import com.vaadin.ui.AbstractSelect
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Component
import com.vaadin.ui.Field
import com.vaadin.ui.Form
import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.Slider
import com.vaadin.ui.Table.TableDragMode
import com.vaadin.ui.TextField
import dela.DataService
import dela.Setup
import dela.State
import dela.StoreService
import dela.Subject
import dela.Task
import dela.YesNoDialog
import dela.meta.MetaProvider
import dela.ui.common.EntityTable

/**
 * @author vedi
 * date 04.07.2010
 * time 20:29:22
 */
public class TaskTable extends EntityTable implements FormFieldFactory, DropHandler  {

    StoreService storeService
    DataService dataService
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
        this.storeService = getBean(StoreService.class)
        this.dataService = getBean(DataService.class)
        this.dropHandler = this
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

    protected void initTable() {
        super.initTable();

        table.addShortcutListener(new ShortcutListener("moveDown", ShortcutAction.KeyCode.ARROW_DOWN, [ShortcutAction.ModifierKey.CTRL] as int[]) {
            void handleAction(Object o, Object o1) {

                def sourceItemId = TaskTable.this.table.value
                def targetItemId = TaskTable.this.container.nextItemId(TaskTable.this.table.value)
                def anotherItemId = TaskTable.this.container.nextItemId(targetItemId)
                moveItem(TaskTable.this.container, targetItemId, anotherItemId, 0.0, sourceItemId)
                TaskTable.this.table.value = targetItemId
            }
        });
        table.addShortcutListener(new ShortcutListener("moveUp", ShortcutAction.KeyCode.ARROW_UP, [ShortcutAction.ModifierKey.CTRL] as int[]) {
            void handleAction(Object o, Object o1) {
                def sourceItemId = TaskTable.this.table.value
                def targetItemId = TaskTable.this.container.prevItemId(TaskTable.this.table.value)
                def anotherItemId = TaskTable.this.container.prevItemId(targetItemId)
                moveItem(TaskTable.this.container, targetItemId, anotherItemId, 1.0, sourceItemId)
                TaskTable.this.table.value = targetItemId
            }
        });

    }



    protected void initToolBar() {
        super.initToolBar()

        completeButton = new Button();
        completeButton.setDescription(i18n('button.complete.label', 'complete'))
        completeButton.setIcon(new FileResource(new File('web-app/images/skin/task_done.png'), this.window.application))
        completeButton.addListener(this as ClickListener)
        toolBarLayout.addComponent(completeButton)
    }

    def void buttonClick(ClickEvent clickEvent) {
        if (clickEvent.button == completeButton) {
            def item = container.getItem(table.value)
            if (item) {
                this.window.application.mainWindow.addWindow(new YesNoDialog(
                        i18n('button.complete.confirm.caption', 'confirm complete'),
                        i18n('button.complete.confirm.message', 'are you sure?'),
                        i18n('button.yes.label', 'yes'),
                        i18n('button.no.label', 'no'),
                        new YesNoDialog.Callback() {
                            public void onDialogResult(boolean yes) {
                                if (yes) {
                                    Long id = item.getItemProperty('id').value as Long
                                    if (dataService.tryCompleteTask(id)) {
                                        this.refresh()
                                    }
                                }
                            }

                        }))
            }
        } else {
            super.buttonClick(clickEvent);
        }
    }

    Field createField(Item item, Object propertyId, Component component) {
        String label = getColumnLabel(propertyId)
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
            Slider slider = new Slider(caption:label,
                    min: 0.01, max: 0.99, resolution: 2, orientation: Slider.ORIENTATION_VERTICAL,
                    immediate: true)

            slider.setHeight "100%"

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

    void drop(DragAndDropEvent dragAndDropEvent) {
        def transferable = dragAndDropEvent.transferable
        Container.Ordered container = transferable.sourceContainer

        if (container.equals(this.table.containerDataSource)) {
            Object sourceItemId = transferable.itemId

            def dropData = dragAndDropEvent.targetDetails
            def targetItemId = dropData.itemIdOver

            if (targetItemId != null) {
                def anotherItemId
                double defaultPowerValue
                if (dropData.dropLocation == VerticalDropLocation.BOTTOM) {
                    anotherItemId = container.nextItemId(targetItemId)
                    defaultPowerValue = 0.0
                } else {
                    anotherItemId = container.prevItemId(targetItemId)
                    defaultPowerValue = 1.0
                }
                if (!targetItemId.equals(anotherItemId)) {
                    moveItem(container, targetItemId, anotherItemId, defaultPowerValue, sourceItemId)
                }
            }
        }
    }

    private def moveItem(Ordered container, targetItemId, anotherItemId, double defaultPowerValue, sourceItemId) {
        double targetPower = container.getItem(targetItemId)?.getItemProperty('power')?.value ?: 0 as double
        double anotherPower = anotherItemId ? (container.getItem(anotherItemId)?.getItemProperty('power')?.value ?: defaultPowerValue) : defaultPowerValue as double
        double newPower = Math.abs(targetPower + anotherPower) / 2.0

        def id = container.getItem(sourceItemId).getItemProperty('id').value as Long

        dataService.changeTaskPower(id, newPower)

        this.refresh()
    }

    AcceptCriterion getAcceptCriterion() {
        return AbstractSelect.AcceptItem.ALL
    }
}
