package com.lab.chattech.controller.ui;

import com.lab.chattech.model.Mensaje;
import com.lab.chattech.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/*
    This controller handles the web page (MVC) side of the application.

    It uses the MVC pattern (Model-View-Controller):
    - Model: data we prepare and send to the HTML template
    - View: the HTML file rendered by Thymeleaf
    - Controller: this class, which connects the data to the view

    DIFFERENCE FROM @RestController:
    - @RestController returns JSON data
    - @Controller returns the NAME of a Thymeleaf template (an HTML file)
      Thymeleaf then processes that HTML file, fills in the dynamic data,
      and sends the final HTML to the browser.

    This is called "server-side rendering" because the HTML is built on the server
    with the actual data already included, before being sent to the browser.

    WHY DO WE NEED THIS IF WE HAVE WEBSOCKETS?
    When the user first opens the chat page, we need to show them the existing
    chat history. WebSocket only handles messages in real-time AFTER connecting.
    Thymeleaf loads the old history from MongoDB BEFORE the page even connects to WebSocket.
    Once the page loads and JavaScript connects to WebSocket, real-time messages flow.
    This gives us the best of both worlds: existing history + real-time new messages.

    @RequestMapping("/admin") means all routes in this class start with "/admin".
*/
@Controller
@RequestMapping("/admin")
public class ChatUIController {

    /*
        Inject the message service to get the conversation history from MongoDB.
    */
    @Autowired
    private MensajeService mensajeService;

    /*
        GET /admin/chat
        This method handles requests to the chat page.

        The Model parameter is provided by Spring automatically.
        It is like a container that we can put data into.
        Thymeleaf will read the data from this Model when it builds the HTML page.

        The method returns a String "chat/sala" which is the path to the template:
        src/main/resources/templates/chat/sala.html
        The .html extension is added automatically by Thymeleaf.
    */
    @GetMapping("/chat")
    public String mostrarSalaDeChat(Model model) {

        // Get all messages from MongoDB to display as the initial history
        List<Mensaje> historial = mensajeService.obtenerTodosLosMensajes();

        /*
            model.addAttribute("historial", historial) puts the list into the model
            with the name "historial".

            In the Thymeleaf template (sala.html), we can then access this list
            using the variable name "historial" in expressions like:
            th:each="msg : ${historial}"

            This is how the server sends data to the HTML template.
        */
        model.addAttribute("historial", historial);

        // Return the name of the Thymeleaf template to render
        // Spring Boot will look for: src/main/resources/templates/chat/sala.html
        return "chat/sala";
    }

}