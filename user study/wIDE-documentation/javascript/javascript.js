function register() {
    if (validate()) {
        alert("Thank you for your registration!");
    }
}

function validate() {
    return validateName() && validateMail();
}

function validateName() {
    var nameNode = document.getElementById("name");
    // TODO 1: validate name (at least two chars, only letters)
    //         RegExp: "^[a-zA-Z]+$"

    // TODO 2: if name is invalid -> create a div-node of class "error"
    //         and append it to the node with id "namediv".

    // TODO 3: if name is valid -> remove the above node again (if present) false;

    return false;

}

function validateMail() {
    var mailNode = document.getElementById("mail");
    // TODO 4: validate mail
    //         [>0 chars] @ [>0 two chars] . [>0 chars]
    //         RegExp: "^[^@]+@[^@\.,;]+\.[^@\.,;]+"

    alert("hi");
    //alert(mailNode.value.match("^[^@]+@[^@\.,;]+\.[^@\.,;]+"));

    return false;

    // TODO 5: if name is invalid -> create a div-node of class "error"
    //         and append it to the node with id "maildiv".

    // TODO 6: if name is valid -> remove the above node again (if present)


}