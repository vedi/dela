package dela.ui.common

import com.vaadin.ui.Window
import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * @author vedi
 * date 12.12.10
 * time 21:48
 */
class UiTools {

    /**
     *
     * @param sessionContext
     * @param dataServiceClass
     * @param params recognized keys are tableFactory, gridFields, dataViewName, formFactory,
     *      formFields (for default form factory)
     * @return
     */
    def createListWindow(sessionContext, Class dataServiceClass, params) {

        new Window() {
            def void attach() {
                super.attach()

                def tableFactory = params.tableFactory?:simpleTableFactory
                def table = tableFactory()

                if (params.gridFields) {
                    table.gridFields = params.gridFields
                }

                if (!params.tableFactory) {
                    if (params.formFactory) {
                        table.formFactory = params.formFactory
                    } else {
                        table.formFactory = simpleFormFactory
                        table.formFactoryParams = params
                    }
                }

                def dataService = ApplicationHolder.application.mainContext.getBean(dataServiceClass)
                if (params.dataViewName) {
                    table.dataContext = dataService.createDataContext(sessionContext, params.dataViewName)
                } else {
                    table.dataContext = dataService.createDataContext(sessionContext)
                }
                table.setWidth("100%")
                table.setHeight("100%")

                this.addComponent(table)

                this.content.setWidth("300px")
                this.content.setHeight("350px")
                this.center()
            }
        }
    }

    def simpleTableFactory = {
        new SimpleEntityTable()
    }

    def simpleFormFactory = {params ->

        def form = new SimpleEntityForm()

        if (params.formFields) {
            form.formFields = params.formFields
        }

        form
    }

}
