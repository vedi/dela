package dela.ui

import com.vaadin.Application
import com.vaadin.data.Container
import com.vaadin.event.dd.DragAndDropEvent
import com.vaadin.event.dd.DropHandler
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation
import com.vaadin.ui.AbstractSelect
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.ComponentContainer
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import dela.Setup
import dela.StoreService
import dela.Task
import dela.meta.MetaProvider
import dela.ui.subject.SubjectListWindow
import dela.ui.task.TaskTable

public class DelaApplication extends Application implements DropHandler {

    private Window mainWindow
    private MetaProvider metaProvider

    def metaDomain
    def table

    StoreService storeService

    @Override
	public void init() {

		mainWindow = new Window("Dela Application");

        HorizontalLayout horizontalLayout = new HorizontalLayout()
        mainWindow.setContent(horizontalLayout)

        initButtons(horizontalLayout)

        storeService = getBean(StoreService.class)
        storeService.setup = loadSetup()

        metaProvider = new MetaProvider(storeService:storeService)

        metaDomain = metaProvider.taskMeta

        table = new TaskTable(metaDomain: metaDomain, dropHandler: this, metaProvider: metaProvider)
        table.setWidth "700"


		mainWindow.addComponent(table)

		setMainWindow(mainWindow)

	}

    def loadSetup() {
        Setup.count() ? Setup.findAll()[0] : new Setup() // TODO: Move to service
    }

    void initButtons(ComponentContainer componentContainer) {
        VerticalLayout verticalLayout = new VerticalLayout()
        verticalLayout.setWidth "120"

        Button button

        button = new Button("subjects", new ClickListener() {
            void buttonClick(ClickEvent clickEvent) {
                DelaApplication.this.mainWindow.addWindow(new SubjectListWindow(metaDomain: metaProvider.subjectMeta))
            }
        })
        verticalLayout.addComponent(button);

        button = new Button("setup", new ClickListener() {
            void buttonClick(ClickEvent clickEvent) {
                DelaApplication.this.mainWindow.addWindow(new SetupWindow())
            }
        })
        verticalLayout.addComponent(button);

        componentContainer.addComponent(verticalLayout)
    }


    void drop(DragAndDropEvent dragAndDropEvent) {
        def transferable = dragAndDropEvent.transferable
        Container.Ordered container = transferable.sourceContainer

        if (container.equals(this.table.containerDataSource)) {
            Object sourceItemId = transferable.itemId

            def dropData = dragAndDropEvent.targetDetails
            def targetItemId = dropData.itemIdOver

            if (targetItemId != null) {
                double targetPower = container.getItem(targetItemId)?.getItemProperty('power')?.value?:0 as double
                def anotherItemId
                double defaultValue
                if (dropData.dropLocation == VerticalDropLocation.BOTTOM) {
                    anotherItemId = container.nextItemId(targetItemId)
                    defaultValue = 0.0
                } else {
                    anotherItemId = container.prevItemId(targetItemId)
                    defaultValue = 1.0
                }
                if (!targetItemId.equals(anotherItemId)) {
                    double anotherPower = anotherItemId?(container.getItem(anotherItemId)?.getItemProperty('power')?.value?:defaultValue):defaultValue as double
                    double newPower = Math.abs(targetPower + anotherPower) / 2.0

                    // TODO: Move to service
                    Task.withTransaction {
                        Task task = Task.get(container.getItem(sourceItemId).getItemProperty('id').value as Long)
                        task.power = newPower;
                        task.save()
                    }

                    this.table.refresh()
                }
            }
        }
    }

    AcceptCriterion getAcceptCriterion() {
        return AbstractSelect.AcceptItem.ALL
    }
}