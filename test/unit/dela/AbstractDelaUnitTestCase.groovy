package dela;

import grails.test.GrailsUnitTestCase;

/**
 * @author vedi
 *         date 10.12.10
 *         time 17:31
 */
public abstract class AbstractDelaUnitTestCase extends GrailsUnitTestCase {

    protected void mockDomain(Class domainClass) {
        super.mockDomain(domainClass)

        domainClass.metaClass.'static'.withTransaction = {closure -> closure()}
        domainClass.metaClass.merge = {delegate}
    }

}
