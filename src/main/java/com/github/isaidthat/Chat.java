package com.github.isaidthat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Chat implements CommandExecutor {
    private final JavaPlugin plugin;

    public Chat(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private static final HttpClient client = HttpClient.newHttpClient();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("only players can use this command");
            return true;
        }

        if (!player.hasPermission("bot.chat")) {
            player.sendMessage("no permission");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("usage: /chat <message>");
            return true;
        }

        String prompt = String.join(" ", args);

        Bukkit.broadcastMessage("%s asked: %s".formatted(sender.getName(), prompt));
        Bukkit.getScheduler().runTaskAsynchronously(
        Bukkit.getPluginManager().getPlugin("minecraft_chatbot"),
                () -> {

                    try {
                        String json = """
                        {
                          "contents": [
                            {
                              "parts": [
                                { "text": "%s" }
                              ]
                            }
                          ]
                        }
                        """.formatted(prompt.replace("\"", "\\\""));

                        String apiKey = plugin.getConfig().getString("apikey");
                        String model = plugin.getConfig().getString("model");


                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s".formatted(model, apiKey)))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(json))
                                .build();

                        HttpResponse<String> response =
                                client.send(request, HttpResponse.BodyHandlers.ofString());

                        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

                        String modelresponse =
                                    root.getAsJsonArray("candidates")
                                        .get(0).getAsJsonObject()
                                        .get("content").getAsJsonObject()
                                        .getAsJsonArray("parts")
                                        .get(0).getAsJsonObject()
                                        .get("text").getAsString();

                        Bukkit.getScheduler().runTask(
                            plugin,
                            () -> Bukkit.broadcastMessage("model replied: %s".formatted(modelresponse))
                        );


                    } catch (Exception e) {
                        e.printStackTrace();

                        Bukkit.getScheduler().runTask(
                                plugin,
                                () -> Bukkit.broadcastMessage("request failed")
                        );
                    }
                }
        );

        return true;
    }
}