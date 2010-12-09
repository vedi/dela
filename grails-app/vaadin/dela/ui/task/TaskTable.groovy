package dela.ui.task

import com.vaadin.data.Container
import com.vaadin.data.Container.Ordered
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
import com.vaadin.ui.Table.TableDragMode
import dela.ui.common.EntityForm
import dela.ui.common.EntityTable
import dela.ui.common.Searcher
import dela.*

/**
 * @author vedi
 * date 04.07.2010
 * time 20:29:22
 */
public class TaskTable extends EntityTable implements DropHandler  {

    Button completeButton

    def gridFields = ['subject', 'name']

    def TaskTable() {
        this.dropHandler = this
    }

    protected def createForm() {
        return new TaskForm()
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
        completeButton.setIcon(new FileResource(getFile('images/skin/task_done.png'), this.window.application))
        completeButton.addListener(this as ClickListener)
        toolBar.addComponent(completeButton)

        Searcher searcher = new Searcher(entityTable:this, application: this.window.application);
        searcher.addTo(toolBar)
    }

    def void buttonClick(ClickEvent clickEvent) {
        if (clickEvent.button == completeButton) {
            if (table.value != null) {
                def item = container.getItem(table.value)
                if (item) {
                    addWindow(new YesNoDialog(
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
        } else {
            super.buttonClick(clickEvent);
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
