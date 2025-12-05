@import java.lang.String
@param String error

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link rel="stylesheet" href="/bulma.css">
</head>
<body>

<div class="container is-max-tablet mt-4">
    <div class="is-flex is-align-items-center" style="width: 100%;">
        <h1 class="title mb-0">Todos</h1>
        <div class="is-flex-grow-1"></div>
        <div style="width: 120px;"></div>
    </div>

    <div class="is-flex is-justify-content-center mt-5">
        <div style="width: 100%; max-width: 420px;">
            @if(error != null && !error.isBlank())
                <article class="message is-danger is-small mb-4">
                    <div class="message-body">
                        ${error}
                    </div>
                </article>
            @endif

            <form action="/login" method="POST">
                <div class="field">
                    <label class="label" for="email">Email</label>
                    <div class="control">
                        <input autofocus class="input" type="email" name="email" id="email" placeholder="you@company.com" required>
                    </div>
                </div>

                <div class="field">
                    <label class="label" for="password">Password</label>
                    <div class="control">
                        <input class="input" type="password" name="password" id="password" placeholder="Your password" required>
                    </div>
                </div>

                <div class="field mt-4">
                    <div class="control has-text-centered">
                        <button class="button is-primary" type="submit">
                            Sign in
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>
