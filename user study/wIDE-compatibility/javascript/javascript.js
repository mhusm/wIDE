function get_todos() {
    var todos = new Array;
    var todos_str = localStorage.getItem('todo');
    if (todos_str !== null) {
        todos = JSON.parse(todos_str);
    }
    return todos;
}

function add() {
    var task = document.getElementById('task').value;
    var taskEntry = {};
    taskEntry.task = document.getElementById('task').value;
    taskEntry.category = document.getElementById('category').options[document.getElementById('category').selectedIndex].value;
    taskEntry.status = '';

    var todos = get_todos();
    todos.push(taskEntry);
    localStorage.setItem('todo', JSON.stringify(todos));

    show();

    return false;
}

function remove() {
    var id = this.getAttribute('id');
    var todos = get_todos();
    todos.splice(id, 1);
    localStorage.setItem('todo', JSON.stringify(todos));

    show();

    return false;
}

function check() {
    var todos = get_todos();
    todos[this.getAttribute('id')].status = 'done';
    localStorage.setItem('todo', JSON.stringify(todos));

    show();

    return false;
}

function uncheck() {
        var todos = get_todos();
        todos[this.getAttribute('id')].status = '';
        localStorage.setItem('todo', JSON.stringify(todos));

        show();

        return false;
}

function show() {
    var todos = get_todos();

    var ul = document.createElement("ul");
    for(var i=0; i<todos.length; i++) {
        var li = document.createElement("li");
        li.setAttribute("id", "list_" + i);
        li.className = "todo";
        li.className = li.className + ' ' + todos[i].category;
        li.innerHTML = todos[i].task;

        var removeButton = document.createElement("button");
        removeButton.setAttribute("id", i);
        removeButton.setAttribute("class", "remove");
        removeButton.innerHTML = "x";

        var checkButton = document.createElement("button");
        checkButton.setAttribute("id", i);

        if (todos[i].status === "done") {
            li.className = li.className + ' done';
            checkButton.innerHTML = '&#x21bb;';
            checkButton.setAttribute("class", "uncheck");
        } else {
            checkButton.innerHTML = '&#10004;';
            checkButton.setAttribute("class", "check");
        }

        li.appendChild(checkButton);
        li.appendChild(removeButton);
        ul.appendChild(li);
    };

    while(document.getElementById('todos').hasChildNodes()) {
        document.getElementById('todos').removeChild(document.getElementById('todos').lastChild);
    }
    document.getElementById('todos').appendChild(ul);

    var checkButtons = document.getElementsByClassName('check');
    for (var i=0; i < checkButtons.length; i++) {
        checkButtons[i].addEventListener('click', check);
    };
    var checkButtons = document.getElementsByClassName('uncheck');
    for (var i=0; i < checkButtons.length; i++) {
        checkButtons[i].addEventListener('click', uncheck);
    };

    var buttons = document.getElementsByClassName('remove');
    for (var i=0; i < buttons.length; i++) {
        buttons[i].addEventListener('click', remove);
    };
}

var addButton = document.getElementById('add');
addButton.addEventListener('click', add);
show();