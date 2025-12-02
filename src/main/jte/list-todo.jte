@import net.codeer.app.shared.Todo
@import java.util.List
@param List<Todo> todos
@param List<Todo> completed
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Todos List</title>
    <link rel="stylesheet" href="/bulma.css">
</head>
<body>
<div class="container is-max-tablet is-flex is-flex-direction-column is-align-items-center mt-4">
    <h1 class="title">Todos</h1>
    <form action="/" method="POST">
        <div class="field has-addons">
            <div class="control">
                <input autofocus class="input" type="text" name="content" id="content" placeholder="Type a todo" required>
            </div>
            <div class="control">
                <button class="button is-primary">
                    Add
                </button>
            </div>
        </div>
    </form>
</div>
<div class="container is-max-tablet mt-4">
    <h2 class="subtitle">Todos</h2>
    @if(todos.isEmpty())
        <p class="my-4">No todos yet!</p>
    @endif
    @for(Todo todo : todos)
        <div class="card">
            <div class="card-header px-2">
                <p class="card-header-title">${todo.getContent()}</p>
                <form action="/${todo.getId()}/done" method="GET" class="ml-1 is-display-block is-flex is-align-items-center">
                    <button class="button is-primary is-small" type="submit">
                        Done
                    </button>
                </form>
            </div>
        </div>
    @endfor
    <h2 class="subtitle">Completed</h2>
    @if(completed.isEmpty())
        <p class="my-4">Nothing completed yet!</p>
    @endif
    @for(Todo todo : completed)
        <div class="card">
            <div class="card-header px-2">
                <del class="card-header-title">${todo.getContent()}</del>
                <form action="/${todo.getId()}/undone" method="GET" class="ml-1 is-display-block is-flex is-align-items-center">
                    <button class="button is-primary is-small" type="submit">
                        Undone
                    </button>
                </form>
                <form action="/${todo.getId()}/delete" method="GET" class="ml-1 is-display-block is-flex is-align-items-center">
                    <button class="button is-danger is-small" type="submit">
                        Delete
                    </button>
                </form>
            </div>
        </div>
    @endfor
</div>

</body>
</html>
