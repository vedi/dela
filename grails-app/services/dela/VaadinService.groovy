package dela

import dela.container.DomainLazyContainer
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.vaadin.terminal.FileResource
import com.vaadin.ui.AbstractField

class VaadinService {

    def metaService

    static transactional = true

    def createDefaultLazyContainer(dataContext) {
        def counter = {
            dataContext.metaDomain.domainClass.count()
        }

        def selector = {startIndex, count, sortProperty, ascendingState ->
            if (sortProperty) {
                dataContext.metaDomain.domainClass.list(offset:startIndex,  max:count, sort:sortProperty, order:ascendingState)
            } else {
                dataContext.metaDomain.domainClass.list(offset:startIndex,  max:count)
            }
        }

        return new DomainLazyContainer(dataContext.metaDomain.domainClass, selector, counter, dataContext.metaDomain.columns)
    }

    def createTaskDefaultContainer(dataContext) {
        def selector = {startIndex, count, sortProperty, ascendingState ->
            if (sortProperty) {
                throw new UnsupportedOperationException()
            }

            Setup setup = dataContext.setup
            (setup.filterStates.size() > 0 && setup.filterSubjects.size() > 0) ?
                Task.findAllByStateInListAndSubjectInList(setup.filterStates, setup.filterSubjects, [offset:startIndex,  max:count, sort:'power', order:'desc']) : []
        }

        def counter = {
            Setup setup = dataContext.setup
            (setup.filterStates.size() > 0 && setup.filterSubjects.size() > 0) ?
                Task.countByStateInListAndSubjectInList(setup.filterStates, setup.filterSubjects) : 0
        }

        return new DomainLazyContainer(dataContext.metaDomain.domainClass, selector, counter, dataContext.metaDomain.columns)
    }

    def createSubjectDefaultContainer(dataContext) {
        def selector = {startIndex, count, sortProperty, ascendingState ->
            Subject.findAllByOwnerOrIsPublic(dataContext.account, true, [offset:startIndex,  max:count, sort:sortProperty, order:ascendingState])
        }

        def counter = {
            Subject.countByOwnerOrIsPublic(dataContext.account, true)
        }

        return new DomainLazyContainer(dataContext.metaDomain.domainClass, selector, counter, dataContext.metaDomain.columns)
    }

    def createAccountDefaultContainer(dataContext) {
        def selector = {startIndex, count, sortProperty, ascendingState ->
            Account.findAll([offset:startIndex,  max:count, sort:sortProperty, order:ascendingState])
        }

        def counter = {
            Account.count()
        }

        return new DomainLazyContainer(dataContext.metaDomain.domainClass, selector, counter, dataContext.metaDomain.columns)
    }

    def getMetaListCaption(dataContext) {
        metaService.getMetaListCaption(dataContext.metaDomain)
    }

    def getMetaCaption(dataContext) {
        metaService.getMetaCaption(dataContext.metaDomain)
    }

    def getColumnCaption(dataContext, column) {
        metaService.getColumnCaption(dataContext.metaDomain, column)
    }

    def List<String> getGridVisibleColumns(dataContext) {
        dataContext.metaDomain.columns.collect {it.field}
    }

    def List<String> getEditVisibleColumns(dataContext) {
        dataContext.metaDomain.columns.collect {it.field}
    }

    def getFile(fileName) {
        return ApplicationHolder.application.parentContext.getResource(fileName).file
    }

    def addDomainValidator(AbstractField field, domain, propertyId) {
        if (field) {
            field.addValidator(new DomainFieldValidator(domain: domain, propertyName: propertyId))
        }
    }

    def addServiceValidator(AbstractField field, dataService, dataContext, domain) {
        if (field) {
            field.addValidator(
                    new ServiceValidator(dataService: dataService, dataContext: dataContext, domain: domain))
        }
    }

}
