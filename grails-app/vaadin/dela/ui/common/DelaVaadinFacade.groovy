package dela.ui.common

import dela.SubjectService
import dela.ui.subject.SubjectTable
import dela.ui.SetupWindow
import dela.AccountService
import dela.ui.account.AccountForm
import javax.annotation.Resource
import com.vaadin.ui.Button
import com.vaadin.terminal.FileResource
import com.vaadin.ui.Button.ClickListener
import dela.Utils
import dela.Subject
import com.vaadin.ui.Button.ClickEvent
import dela.Setup
import dela.Account

/**
 * @author vedi
 * date 16.12.10
 * time 18:35
 */
@Mixin(Utils)
class DelaVaadinFacade {

    @Resource
    def uiTools

    @Resource
    def messageService

    def createSubjectListButton(sessionContext, application) {
        createActionButton(
                application,
                Subject,
                new ClickListener() {
                    @Override
                    void buttonClick(ClickEvent clickEvent) {
                        showSubjectListWindow(sessionContext, application.mainWindow)
                    }
        })

    }

    def createOwnSetupButton(sessionContext, application) {
        createActionButton(
                application,
                Setup,
                new ClickListener() {
                    @Override
                    void buttonClick(ClickEvent clickEvent) {
                        showOwnSetupWindow(sessionContext, application.mainWindow)
                    }

                },
                'images/skin/blue_config.png')
    }

    def createAccountListButton(sessionContext, application) {
        createActionButton(
                application,
                Account,
                new ClickListener() {
                    @Override
                    void buttonClick(ClickEvent clickEvent) {
                        showAccountListWindow(sessionContext, application.mainWindow)
                    }

                }
        )
    }

    private def showSubjectListWindow(sessionContext, mainWindow) {
        mainWindow.addWindow(uiTools.createListWindow(
                sessionContext, SubjectService,
                [
                        tableFactory: {new SubjectTable()},
                        dataViewName: SubjectService.OWN_AND_PUBLIC_DATA_VIEW,
                ]))
    }

    private def showOwnSetupWindow(sessionContext, mainWindow) {
        mainWindow.addWindow(new SetupWindow(sessionContext: sessionContext))
    }

    private def showAccountListWindow(sessionContext, mainWindow) {
        mainWindow.addWindow(uiTools.createListWindow(
                sessionContext, AccountService, [
                        gridFields: ['login', 'email'],
                        formFactory: {new AccountForm()}
                ]))

    }

    private def createActionButton(application, domainClass, ClickListener clickListener,
                                   iconFilePath = 'images/skin/category.png') {

        def button = new Button();
        button.caption = messageService.getEntityListCaptionMsg(domainClass.simpleName.toLowerCase())
        button.setIcon(new FileResource(getFile(iconFilePath), application))
        button.setWidth('100%')
        button.addStyleName('actionButton')
        button.addListener(clickListener)

        button
    }

}
