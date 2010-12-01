package dela

import grails.test.GrailsUnitTestCase

class SubjectTests extends GrailsUnitTestCase {

    private DomainFactory domainFactory = new DomainFactory()

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSubject() {
        mockDomain(Subject)
        def subject = domainFactory.createSubject()
        boolean result = subject.validate()
        assertTrue(subject.errors.toString(), result)
    }

    void testNameConstraints() {
        mockDomain(Subject)

        def subject = domainFactory.createSubject()
        assert subject.save()


        def anotherSubject = domainFactory.createSubject(name: subject.name)
        assertFalse(anotherSubject.validate())
        assertEquals('unique', anotherSubject.errors['name'])

        anotherSubject.name = null
        assertFalse(anotherSubject.validate())
        assertEquals('nullable', anotherSubject.errors['name'])

        anotherSubject.name = '123'
        assertTrue(anotherSubject.validate())
    }

}
