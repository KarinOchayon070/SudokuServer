package com.hit.server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hit.controller.SudokuController;
import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Scanner;


public class HandleRequest implements Runnable{
	
	Socket socket;
	Scanner reader;
    PrintWriter writer;
    Gson gson = new GsonBuilder().create();
    SudokuController controller = new SudokuController();


    public HandleRequest(Socket socket) throws IOException {
        this.socket = socket;
        reader = new Scanner((new InputStreamReader(socket.getInputStream())));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run(){
    	
        try{
            Type type = new TypeToken<Request>(){}.getType();
            Request request = gson.fromJson(reader.nextLine(), type);
            Response response = null;

            String command = request.getHeaders().get("action");

            switch (command) {
            	case "getAllTemplates":{
            		response = new Response(controller.getAllTemplates());
            		break;
            	}
                case "templateByDifficulty":{
                	response = new Response(controller.getTemplateByDifficulty(request.getBody()));
                	break;
                }
                case "templateByID": {
                    response = new Response(controller.getTemplateById(request.getBody()));
                    break;
                }
                case "deleteByID": {
                	controller.delete(request.getBody());
                    response = new Response();
                    break;
                }
                case "solveSudoku": {
                    response = new Response(controller.solveSudoku(request.getBody()));
                    break;
                }
            }
            if(response != null) {
                System.out.println("response "+gson.toJson(response));
                writer.println(gson.toJson(response));
                writer.flush();
            }
            //Must close streams after using
            writer.close();
            reader.close();
            socket.close();
        } catch (Exception e){
            System.out.println("Server Error");
            Response response = new Response("Error: " + e.getMessage(), false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }

}
