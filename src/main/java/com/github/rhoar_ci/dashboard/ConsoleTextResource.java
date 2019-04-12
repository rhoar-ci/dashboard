package com.github.rhoar_ci.dashboard;

import com.github.rhoar_ci.dashboard.jenkins.JenkinsClient;
import org.fusesource.jansi.HtmlAnsiOutputStream;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.StreamingOutput;
import java.io.PrintWriter;

@Path("/console-text/{buildName}/{buildNumber}")
public class ConsoleTextResource {
    public static String link(String buildName, int buildNumber) {
        return "/console-text/" + buildName + "/" + buildNumber;
    }

    @Inject
    private JenkinsClient jenkins;

    @GET
    @Produces("text/html;charset=utf-8") // Jenkins responds in UTF-8 too
    public StreamingOutput get(@PathParam("buildName") String buildName, @PathParam("buildNumber") String buildNumber) {
        return out -> {
            PrintWriter writer = new PrintWriter(out);
            writer.println("<html>");
            writer.println("<head>");
            writer.println("  <meta charset=\"utf-8\">");
            writer.println("  <title>" + buildName + " #" + buildNumber + "</title>");
            writer.println("</head>");
            writer.println("<body style=\"color: #f3f3f3; background-color: #272822\">");
            writer.println(" <pre>");
            writer.flush();
            jenkins.streamConsoleText(buildName, buildNumber).writeTo(new HtmlAnsiOutputStream(out));
            out.flush();
            writer.println(" </pre>");
            writer.println("</body>");
            writer.println("</html>");
            writer.flush();
        };
    }
}
