package dela.ui

import com.vaadin.Application
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.ComponentContainer
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import dela.DataService
import dela.StoreService
import dela.meta.MetaProvider
import dela.ui.subject.SubjectListWindow
import dela.ui.task.TaskTable

public class DelaApplication extends Application {

    private Window mainWindow
    private MetaProvider metaProvider

    def metaDomain
    def table

    StoreService storeService
    DataService dataService 

    @Override
	public void init() {

		mainWindow = new Window("Dela Application");

        HorizontalLayout horizontalLayout = new HorizontalLayout()
        mainWindow.setContent(horizontalLayout)

        initButtons(horizontalLayout)

        storeService = getBean(StoreService.class)
        dataService = getBean(DataService.class)

        storeService.setup = dataService.loadSetup()

        metaProvider = new MetaProvider(storeService:storeService)

        metaDomain = metaProvider.taskMeta

        table = new TaskTable(metaDomain: metaDomain, metaProvider: metaProvider)
        table.setWidth "700"


		mainWindow.addComponent(table)

		setMainWindow(mainWindow)

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

}