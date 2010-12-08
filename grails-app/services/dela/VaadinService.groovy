package dela

import dela.container.DomainLazyContainer
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.vaadin.terminal.FileResource
import com.vaadin.ui.AbstractField

class VaadinService {

    static transactional = true

    def createDefaultLazyContainer(dataContext) {
        def counter = {
            dataContext.domainClass.count()
        }

        def selector = {startIndex, count, sortProperty, ascendingState ->
            if (sortProperty) {
                dataContext.domainClass.list(offset:startIndex,  max:count, sort:sortProperty, order:ascendingState)
            } else {
                dataContext.domainClass.list(offset:startIndex,  max:count)
            }
        }

        return new DomainLazyContainer(dataContext.domainClass, selector, counter,
                dataContext.domainClass.properties) // TODO: Test
    }

    def createTaskDefaultContainer(dataContext, columns) {
        def selector = {startIndex, count, sortProperty, ascendingState ->
            if (sortProperty) {
                throw new UnsupportedOperationException()
            }

            Setup setup = dataContext.setup
            (setup.filterStates.size() > 0 && setup.filterSubjects.size() > 0) ?
                Task.findAllByStateInListAndSubjectInList(
                        setup.filterStates,
                        setup.filterSubjects,
                        [offset:startIndex,  max:count, sort:'power', order:'desc']) :
                []
        }

        def counter = {
            Setup setup = dataContext.setup
            (setup.filterStates.size() > 0 && setup.filterSubjects.size() > 0) ?
                Task.countByStateInListAndSubjectInList(setup.filterStates, setup.filterSubjects) : 0
        }

        return new DomainLazyContainer(dataContext.domainClass, selector, counter, columns)
    }

    def createSubjectDefaultContainer(dataContext, columns) {
        def selector = {startIndex, count, sortProperty, ascendingState ->
            Subject.findAllByOwnerOrIsPublic(dataContext.account, true, [offset:startIndex,  max:count, sort:sortProperty, order:ascendingState])
        }

        def counter = {
            Subject.countByOwnerOrIsPublic(dataContext.account, true)
        }

        return new DomainLazyContainer(dataContext.domainClass, selector, counter, columns)
    }

    def createAccountDefaultContainer(dataContext, columns) {
        def selector = {startIndex, count, sortProperty, ascendingState ->
            Account.findAll([offset:startIndex,  max:count, sort:sortProperty, order:ascendingState])
        }

        def counter = {
            Account.count()
        }

        return new DomainLazyContainer(dataContext.domainClass, selector, counter, columns)
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
