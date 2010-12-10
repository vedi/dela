package dela

import grails.test.GrailsUnitTestCase
import dela.utils.CommandFactory

/**
 * @author vedi
 * date 03.12.10
 * time 18:21
 */
class SubjectCommandTests extends GrailsUnitTestCase {

    def commandFactory = new CommandFactory()

    void testCreateSubjectCommand() {
        mockForConstraintsTests(SubjectCommand)
        def subjectCommand = commandFactory.createSubjectCommand()
        boolean result = subjectCommand.validate()
        assertTrue(subjectCommand.errors.toString(), result)
    }

    void testCreateSubjectCommandFromSubject() {
        mockDomain(Subject)
        Subject.metaClass.'static'.withTransaction = {closure -> closure()}

        def subject = commandFactory.createSubject()

        def subjectCommand = new SubjectCommand(subject)
        assertEquals(subject.id,          subjectCommand.id)
        assertEquals(subject.name,        subjectCommand.name)
        assertEquals(subject.description, subjectCommand.description)
        assertEquals(subject.owner,       subjectCommand.owner)
    }

    void testGetSubjectForNew() {
        mockDomain(Subject)
        Subject.metaClass.'static'.withTransaction = {closure -> closure()}

        mockForConstraintsTests(SubjectCommand)

        def subjectCommand = commandFactory.createSubjectCommand(id:null)

        def subject = subjectCommand.getSubject()

        assertEquals(subjectCommand.name,        subject.name)
        assertEquals(subjectCommand.description, subject.description)
        assertEquals(subjectCommand.owner,       subject.owner)

    }

    void testGetSubject() {
        mockDomain(Subject)
        Subject.metaClass.'static'.withTransaction = {closure -> closure()}
        Subject.metaClass.merge = {delegate}
        mockForConstraintsTests(SubjectCommand)

        def subject = commandFactory.createSubject()
        assert subject.save()

        def subjectCommand = new SubjectCommand(subject)

        subject = subjectCommand.getSubject()

        assertEquals(subjectCommand.id,          subject.id)
        assertEquals(subjectCommand.name,        subject.name)
        assertEquals(subjectCommand.description, subject.description)
        assertEquals(subjectCommand.owner,       subject.owner)

    }

    void testNameConstraints() {
        mockForConstraintsTests(SubjectCommand)
        def subjectCommand = commandFactory.createSubjectCommand(name: null)
        assertFalse(subjectCommand.validate())
        assertEquals('nullable', subjectCommand.errors['name'])
        subjectCommand.name = 'the name'

        assert subjectCommand.validate()
    }

    void testOwnerConstraints() {
        mockForConstraintsTests(SubjectCommand)
        def subjectCommand = commandFactory.createSubjectCommand(owner: null)
        assertFalse(subjectCommand.validate())
        assertEquals('nullable', subjectCommand.errors['owner'])
        subjectCommand.owner = commandFactory.createAccount()

        assert subjectCommand.validate()
    }

    void testDescriptionConstraints() {
        mockForConstraintsTests(SubjectCommand)
        def subjectCommand = commandFactory.createSubjectCommand(description: null)
        assertTrue(subjectCommand.validate())
    }

}
