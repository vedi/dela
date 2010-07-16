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
import dela.Setup
import dela.StoreService
import dela.Subject
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

        button = new Button()
        button.caption = i18n("entity.${Subject.simpleName.toLowerCase()}.many.caption", "${Subject.simpleName} list")
        button.addListener(
                new ClickListener() {
                    void buttonClick(ClickEvent clickEvent) {
                        DelaApplication.this.mainWindow.addWindow(new SubjectListWindow(metaDomain: metaProvider.subjectMeta))
                    }
                })
        verticalLayout.addComponent(button);

        button = new Button()
        button.caption = i18n("entity.${Setup.simpleName.toLowerCase()}.many.caption", "${Setup.simpleName} list")
        button.addListener(
                new ClickListener() {
                    void buttonClick(ClickEvent clickEvent) {
                        DelaApplication.this.mainWindow.addWindow(new SetupWindow())
                    }
                })
        verticalLayout.addComponent(button);

        componentContainer.addComponent(verticalLayout)
    }

}