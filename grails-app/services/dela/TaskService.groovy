package dela

import dela.context.DataContext
import dela.meta.MetaColumn

class TaskService extends DataService<Task> {

    static OWN_TASKS_DATA_VIEW = 'ownTasks'
    static transactional = true

    def columns = [
                    new MetaColumn(field: 'id', type:Long.class, readOnly: true),
                    new MetaColumn(field: 'name', readOnly: true),
                    new MetaColumn(field: 'description'),
                    new MetaColumn(field: 'power', type:Double.class, ),
                    new MetaColumn(field: 'subject', type:Subject.class),
                    new MetaColumn(field: 'state', type:State.class),
                    new MetaColumn(field: 'dateCreated', type:Date.class),
            ]

    def dataViewFactories = [(OWN_TASKS_DATA_VIEW): {dataContext ->
            new DataView(
                    selector: {startIndex, count, sortProperty, ascendingState ->
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
                    },
                    counter: {
                        Setup setup = dataContext.setup
                        (setup.filterStates.size() > 0 && setup.filterSubjects.size() > 0) ?
                            Task.countByStateInListAndSubjectInList(setup.filterStates, setup.filterSubjects) : 0
                    }
            )
        }]

    TaskService() {
        super(Task)
    }

    def Task create(DataContext dataContext) {

        Task task = new Task()
        task.author = dataContext.account as Account
        task.subject = dataContext.setup.activeSubject
        task.state = State.findAll()[0]  // FIXME: Жёсткая привязка к id состояния

        return task
    }

    def Boolean canInsert(DataContext dataContext) {
        return dataContext.account.isNotAnonymous()
    }

    def Boolean canEdit(DataContext dataContext, Task domain) {
        def author = domain.author
        def subject = domain.subject
        return dataContext.account.isAdmin() || (author.equals(dataContext.account) &&
                (subject.isPublic || subject.owner.equals(domain.author)))
    }

    def public changeTaskPower(Task task, double newPower) {
        Task.withTransaction {
            task.power = newPower;
            def result = task.merge()
            assert result, task.errors
        }
    }

    boolean tryCompleteTask(Task task) {
        assert task
        Task.withTransaction {
            State state = State.get(2) // FIXME: Hardcoded state id
            assert state
            if (!state.equals(task.state)) {
                task.state = state
                def result = task.merge()
                assert result, task.errors
                return true
            } else {
                return false
            }
        }
    }

    def normalizeSubjectTasks(subject) {
        Task.withTransaction {
            assert subject

            State state = State.get(1) // TODO: Отвязать от id состояния
            assert state

            def count = Task.countBySubjectAndState(subject, state)
            if (count) {
                double step = 1.0 / (count + 1)
                double currentPower = step
                def tasks = Task.findAllBySubjectAndState(subject, state, [sort:'power', order:'asc'])
                tasks.each {task ->
                    task.power = currentPower
                    currentPower += step
                    def result = task.merge()
                    assert result, task.errors
                }
            }
        }
    }
}
