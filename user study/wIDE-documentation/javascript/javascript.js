function register() {
    if (validate()) {
        alert("Thank you for your registration!");
    }
}

function validate() {
    var success;
    var mail = document.getElementById("mail").value;
    // TODO 1: validate email address
    //         -> [>0 chars] @ [>0 two chars] . [>0 chars]
    //         -> Hint: RegExp: "^[^@]+@[^@\.,;]+\.[^@\.,;]+$"

    // TODO 2: show an error message if not valid
    //         -> create a node of class "error" with id "error"
    //            and append it to the div with class "maildiv"
    //         -> Hint 1: the document object helps you a lot!
    //         -> Hint 2: don't append, if already present!

    // TODO 3: hide error message if valid
    //         -> get node with id "error"
    //            and remove it from parent node with id "maildiv"

    return success;
}