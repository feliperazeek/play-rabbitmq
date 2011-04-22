package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

import play.modules.rabbitmq.producer.*;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void publish(String q) {
    	RabbitMQPublisher.publish("myQueue", q);
    	render(q);
    }

}
