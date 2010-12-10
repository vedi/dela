package dela.utils

/**
 * @author vedi
 * date 06.12.10
 * time 6:43
 */
abstract class AbstractTestFactory {

    public improveMockDomain(domainClass) {
        domainClass.metaClass.'static'.withTransaction = {closure -> closure()}
        domainClass.metaClass.merge = {delegate}

    }

    protected def applyParams(domain, params = [:]) {
        if (params) {
            domain.properties = params
            if (params.save) {
                domain.save()
            }
        }
    }

}
