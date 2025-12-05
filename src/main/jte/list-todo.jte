@import net.codeer.app.common.Todo
@import net.codeer.app.common.UserInfo
@import java.util.List
@param List<Todo> todos
@param List<Todo> completed
@param UserInfo user
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Todos List</title>
    <link rel="stylesheet" href="/bulma.css">
</head>
<body>
<div class="container is-max-tablet is-flex is-flex-direction-column is-align-items-center mt-4">
    <div class="is-flex is-justify-content-space-between is-align-items-center" style="width: 100%;">
        <h1 class="title mb-0">Todos</h1>

        <div class="is-flex is-align-items-center">
        <span class="is-size-7 has-text-grey mr-2" style="opacity: 0.8;">
            ${user.email()}
        </span>
            <form action="/logout" method="GET" class="is-flex is-align-items-center">
                <button class="button is-light is-small" type="submit">
                    Logout
                </button>
            </form>
        </div>
    </div>

    <form action="/" method="POST">
        <div class="field has-addons">
            <div class="control">
                <input autofocus class="input" type="text" name="content" id="content" placeholder="Type a todo"
                       required>
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
            <div class="card-header px-2 is-align-items-center">
                <p class="card-header-title is-flex is-justify-content-space-between is-align-items-center">
                    <span>${todo.getContent()}</span>

                    <span class="tag is-light is-size-7" style="opacity: 0.75;">
                    ${todo.getCreatedBy()}
                </span>
                </p>

                <form action="/${todo.getId()}/done" method="GET"
                      class="ml-1 is-display-block is-flex is-align-items-center">
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
            <div class="card-header px-2 is-align-items-center">
                <p class="card-header-title is-flex is-justify-content-space-between is-align-items-center">
                    <span><del>${todo.getContent()}</del></span>

                    <span class="tag is-light is-size-7" style="opacity: 0.75;">
                    ${todo.getCreatedBy()}
                </span>
                </p>

                <form action="/${todo.getId()}/undone" method="GET"
                      class="ml-1 is-display-block is-flex is-align-items-center">
                    <button class="button is-primary is-small" type="submit">
                        Undone
                    </button>
                </form>
                <form action="/${todo.getId()}/delete" method="GET"
                      class="ml-1 is-display-block is-flex is-align-items-center">
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
