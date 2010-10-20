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

import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.Slider
import com.vaadin.ui.Table.TableDragMode
import com.vaadin.ui.TextField
import dela.Setup

import dela.Subject
import dela.Task
import dela.VaadinService
import dela.YesNoDialog
import dela.ui.SetupWindow
import dela.ui.common.EntityTable
import dela.ui.common.Searcher
import dela.ui.subject.SubjectListWindow
import dela.IDataService
import dela.TaskService
import dela.context.DataContext
import dela.MessageService
import dela.ui.common.EntityForm
import com.vaadin.ui.Form

/**
 * @author vedi
 * date 04.07.2010
 * time 20:29:22
 */
public class TaskTable extends EntityTable implements FormFieldFactory, DropHandler  {

    VaadinService vaadinService
    MessageService messageService

    Button completeButton
    Button subjectButton
    Button setupButton

    def gridVisibleColumns = ['subject', 'name']

    def editVisibleColumns = ['subject', 'name', 'description', 'state', 'power']

    def formFieldFactory = this

    def TaskTable() {
        this.vaadinService = getBean(VaadinService.class)
        this.messageService = getBean(MessageService.class)
        this.dropHandler = this
    }

    protected Form createForm() {
        return new TaskForm()
    }

    protected Container createContainer(DataContext dataContext) {
        return vaadinService.createTaskDefaultContainer(dataContext)
    }

    protected IDataService initDataService() {
        return getBean(TaskService.class)
    }

    protected createDomain() {

        Task task = super.createDomain() as Task

        // Add to the top of the current selection
        def firstItemId = container.firstItemId()
        double firstPower = firstItemId != null ? container.getItem(firstItemId)?.getItemProperty('power')?.value?:0.0 as double : 0.0
        task.power = (1.0 + firstPower) / 2

        return task
    }

    def initGrid() {
        this.table.setColumnWidth 'subject', 120
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

    protected void initToolBar(toolBar) {
        super.initToolBar(toolBar)

        completeButton = new Button();
        completeButton.setDescription(messageService.getCompleteButtonLabel())
        completeButton.setIcon(new FileResource(vaadinService.getFile('images/skin/task_done.png'), this.window.application))
        completeButton.addListener(this as ClickListener)
        toolBar.addComponent(completeButton)

        subjectButton = new Button();
        subjectButton.description = messageService.getEntityListCaptionMsg(Subject.simpleName.toLowerCase())
        subjectButton.setIcon(new FileResource(vaadinService.getFile('images/skin/category.png'), this.window.application))
        subjectButton.addListener(this as ClickListener)
        toolBar.addComponent(subjectButton)

        setupButton = new Button();
        setupButton.description = messageService.getEntityListCaptionMsg(Setup.simpleName.toLowerCase())
        setupButton.setIcon(new FileResource(vaadinService.getFile('images/skin/blue_config.png'), this.window.application))
        setupButton.addListener(this as ClickListener)
        toolBar.addComponent(setupButton)

        Searcher searcher = new Searcher(entityTable:this, application: this.window.application);
        searcher.addTo(toolBar)
    }

    def void buttonClick(ClickEvent clickEvent) {
        if (clickEvent.button == completeButton) {
            if (table.value != null) {
                def item = container.getItem(table.value)
                if (item) {
                    this.window.application.mainWindow.addWindow(new YesNoDialog(
                            messageService.getCompleteConfirmCaption(),
                            messageService.getCompleteConfirmMsg(),
                            messageService.getYesButtonLabel(),
                            messageService.getNoButtonLabel(),
                            new YesNoDialog.Callback() {
                                public void onDialogResult(boolean yes) {
                                    if (yes) {
                                        if (TaskTable.this.dataService.tryCompleteTask(getDomain(item))) {
                                            TaskTable.this.refresh()
                                        }
                                    }
                                }

                            }))
                }
            }

        } else if (clickEvent.button == subjectButton) {
            this.window.application.mainWindow.addWindow(
                    new SubjectListWindow(sessionContext: dataContext.sessionContext))
        } else if (clickEvent.button == setupButton) {
            this.window.application.mainWindow.addWindow(new SetupWindow(sessionContext: dataContext.sessionContext))
        } else {
            super.buttonClick(clickEvent);
        }
    }

    // TODO: Move to form
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

        dataService.changeTaskPower(getDomain(container.getItem(sourceItemId)), newPower)

        this.refresh()
    }

    AcceptCriterion getAcceptCriterion() {
        return AbstractSelect.AcceptItem.ALL
    }
}
