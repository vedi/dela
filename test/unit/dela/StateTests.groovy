package dela

import dela.utils.DomainFactory


class StateTests extends AbstractDelaUnitTestCase {

    def domainFactory = new DomainFactory()

    void testCreateState() {
        mockDomain(State)
        def state = domainFactory.createState()
        boolean result = state.validate()
        assertTrue(state.errors.toString(), result)
    }

    void testLoginConstraints() {
        mockDomain(State)
        def state = domainFactory.createState()
        assert state.save()

        def anotherState = domainFactory.createState(name: state.name)
        assertFalse(anotherState.validate())
        assertEquals('unique', anotherState.errors['name'])

        anotherState.name = null
        assertFalse(anotherState.validate())
        assertEquals('nullable', anotherState.errors['name'])
    }
}
