package dela

import dela.context.DataContext

class TaskService extends DataService<Task> {

    static transactional = true

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
