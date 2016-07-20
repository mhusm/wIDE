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

    var todos = get_todos();
    todos.push(task);
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
    var entry = document.getElementById("list_" + this.getAttribute('id'));
    entry.className = "todo_done";
}

function show() {
    var todos = get_todos();

    var html = '<ul>';
    for(var i=0; i<todos.length; i++) {
        html += '<li id="list_' + i +'">' + todos[i] + '<button class="check" id="' + i  + '">&#10004;</button><button class="remove" id="' + i  + '">x</button></li>';
    };
    html += '</ul>';

    document.getElementById('todos').innerHTML = html;

    var checkButtons = document.getElementsByClassName('check');
    for (var i=0; i < checkButtons.length; i++) {
        checkButtons[i].addEventListener('click', check);
    };

    var buttons = document.getElementsByClassName('remove');
    for (var i=0; i < buttons.length; i++) {
        buttons[i].addEventListener('click', remove);
    };
}

var addButton = document.getElementById('add');
addButton.addEventListener('click', add);
show();