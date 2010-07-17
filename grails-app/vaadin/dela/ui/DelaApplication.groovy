package dela.ui

import com.vaadin.Application
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.ComponentContainer
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import dela.Account
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

    Label stateLabel

    StoreService storeService
    DataService dataService 

    @Override
	public void init() {

		mainWindow = new Window("Dela");

        VerticalLayout verticalLayout = new VerticalLayout()
        mainWindow.setContent(verticalLayout)

        initTopPanel(verticalLayout)

        HorizontalLayout horizontalLayout = new HorizontalLayout()
        verticalLayout.addComponent(horizontalLayout)

        initButtons(horizontalLayout)

        storeService = getBean(StoreService.class)
        dataService = getBean(DataService.class)

        metaProvider = new MetaProvider(storeService:storeService)

        metaDomain = metaProvider.taskMeta

        table = new TaskTable(metaDomain: metaDomain, metaProvider: metaProvider)
        table.setWidth "700"


		horizontalLayout.addComponent(table)

		setMainWindow(mainWindow)

	}

    void initTopPanel(layout) {
        HorizontalLayout horizontalLayout = new HorizontalLayout()
        horizontalLayout.setWidth "100%"
        horizontalLayout.setMargin true

        Button loginButton = new Button(i18n('button.login.label', 'login'))
        loginButton.addListener(new ClickListener() {
            void buttonClick(ClickEvent clickEvent) {
                storeService.origAccount = Account.get(2)
                stateLabel.caption = storeService.account
            }
        })
        horizontalLayout.addComponent(loginButton)
        horizontalLayout.setComponentAlignment(loginButton, Alignment.TOP_RIGHT)

        stateLabel = new Label("${storeService.account}")
        stateLabel.setWidth null
        horizontalLayout.addComponent(stateLabel)
        horizontalLayout.setComponentAlignment(stateLabel, Alignment.TOP_RIGHT)

        layout.addComponent(horizontalLayout)
    }

    void initButtons(ComponentContainer componentContainer) {
        VerticalLayout verticalLayout = new VerticalLayout()
        verticalLayout.setMargin true
        verticalLayout.setWidth "120px"

        Button button

        button = createButton(verticalLayout)
        button.caption = i18n("entity.${Subject.simpleName.toLowerCase()}.many.caption", "${Subject.simpleName} list")
        button.addListener(
                new ClickListener() {
                    void buttonClick(ClickEvent clickEvent) {
                        DelaApplication.this.mainWindow.addWindow(new SubjectListWindow(metaDomain: metaProvider.subjectMeta))
                    }
                })

        button = createButton(verticalLayout)
        button.caption = i18n("entity.${Setup.simpleName.toLowerCase()}.many.caption", "${Setup.simpleName} list")
        button.addListener(
                new ClickListener() {
                    void buttonClick(ClickEvent clickEvent) {
                        DelaApplication.this.mainWindow.addWindow(new SetupWindow())
                    }
                })

        componentContainer.addComponent(verticalLayout)
    }

    private Button createButton(layout) {
        def button = new Button()
        button.setWidth "100%"

        layout.addComponent(button);
//        layout.setComponentAlignment(button, Alignment.TOP_CENTER);

        button
    }

}