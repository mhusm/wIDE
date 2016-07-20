function register() {
    if (validate()) {
        alert("Thank you for your registration!");
    }
}

function validate() {
    var mail = document.getElementById("mail").value;
    //         [>0 chars] @ [>0 two chars] . [>0 chars]
    //         RegExp: "^[^@]+@[^@\.,;]+\.[^@\.,;]+$"
    var success;

    // remove error message (if there)
    var error = document.getElementById("error");
    if (error !== null) {
        document.getElementById("maildiv").removeChild(error);
    }

    // TODO 4: validate mail
    success = (mail.match("^[^@]+@[^@\.,;]+\.[^@\.,;]+$") !== null);


    // TODO 5: if name is invalid -> create a div-node with:
    //        class="error"
    //        id="error"
    //        and append it to the node with id "maildiv".
    //        Hint: It works similar to the remove approach.
    if (!success) {
        var node = document.createElement("div");
        node.setAttribute("id", "error");
        node.setAttribute("class", "error");
        node.innerHTML = "Please provide a valid emailaddress!";
        document.getElementById("maildiv").appendChild(node);
    }

    return success;
}