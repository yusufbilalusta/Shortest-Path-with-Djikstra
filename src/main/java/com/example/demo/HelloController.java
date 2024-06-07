package com.example.demo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;


import java.util.*;


public class HelloController {

    @FXML
    private AnchorPane sol_vbox_anchor;

    @FXML
    private ImageView image_home1;
    @FXML
    private ImageView image_mosque1;
    @FXML
    private ImageView image_restaurant1;
    @FXML
    private ImageView image_school1;
    @FXML
    private ImageView image_wc1;
    @FXML
    private ImageView image_barrier1;
    @FXML
    private ImageView image_barrier21;

    /*
    Aşağıdaki kodda graph tanımlanmıştır. Bu graph komşuluk matrisi yöntemi ile değil kendi yazmış olduğumuz
    LinkedListMap yapısı ile oluşturulmuştur.

    İlk parametre mevut node u.İkinci parametre komşu nodlarını ve bu komşuların bizim node umuza olan
    uzaklığını sakladığı bir LinkedListMapde tutulur.
     */
    private LinkedListMap<ImageView, LinkedListMap<ImageView, Integer>> graph = new LinkedListMap<>();
    //Nodeların isim lerine göre tutulması için nodeNameMap oluşturulur.
    private LinkedListMap<String, ImageView> nodeNameMap = new LinkedListMap<>();
    private double xOffset, yOffset;



    @FXML
    void initialize() {
        nodeNameMap.put("image_home1", image_home1);
        nodeNameMap.put("image_mosque1", image_mosque1);
        nodeNameMap.put("image_restaurant1", image_restaurant1);
        nodeNameMap.put("image_school1", image_school1);
        nodeNameMap.put("image_wc1", image_wc1);
        nodeNameMap.put("image_barrier1", image_barrier1);
        nodeNameMap.put("image_barrier21", image_barrier21);
        //Bütün nodelar sürüklenebilir bir hale getirilir.
        for (ImageView node : nodeNameMap.values()) {
            OnMouseDragged(node);
            OnMousePressed(node);
        }
    }

    void OnMousePressed(Node node) {
        node.setOnMousePressed(event -> {
            //getSceneX ekrandaki koordinatlarını döner
            //event fare imleci
            //layoutX düğümün ekrandaki konumu
            xOffset = event.getSceneX() - node.getLayoutX();
            yOffset = event.getSceneY() - node.getLayoutY();
        });
    }

    void OnMouseDragged(Node node) {
        node.setOnMouseDragged(event -> {
            node.setLayoutX(event.getSceneX() - xOffset);
            node.setLayoutY(event.getSceneY() - yOffset);
            updateGraph((ImageView) node); // ImageView lar sürüklendiğinde Graphı günceller

        });
    }

    //Aslında ekranda bütün image lar duruyor ancak görünür değil biz tıkladığımızda görünür hale gelir.
    @FXML
    void VisibleHome(ActionEvent event) {
        image_home1.setVisible(true);
    }

    @FXML
    void VisibleMosque(ActionEvent event) {
        image_mosque1.setVisible(true);
    }

    @FXML
    void VisibleRestaurant(ActionEvent event) {
        image_restaurant1.setVisible(true);
    }

    @FXML
    void VisibleSchool(ActionEvent event) {
        image_school1.setVisible(true);
    }

    @FXML
    void VisibleBarrier(ActionEvent event) {
        image_barrier1.setVisible(true);
    }

    @FXML
    void VisibleBarrier2(ActionEvent event) {
        image_barrier21.setVisible(true);
    }

    @FXML
    void VisibleWc(ActionEvent event) {
        image_wc1.setVisible(true);
    }

    public void updateGraph(ImageView node) {
        if (graph.containsKey(node)) {
            LinkedListMap<ImageView, Integer> neighbors = graph.get(node);


            List<Text> textToRemove = new ArrayList<>();
            for (Node child : sol_vbox_anchor.getChildren()) {
                if (child instanceof Text) {
                    textToRemove.add((Text) child);
                }
            }
            sol_vbox_anchor.getChildren().removeAll(textToRemove);

            // Siyah çizgileri kaldırın (önceden vurgulanan yol)
            List<Line> blackLinesToRemove = new ArrayList<>();
            for (Node child : sol_vbox_anchor.getChildren()) {
                if (child instanceof Line) {
                    Line line = (Line) child;
                    if (line.getStroke().equals(Color.BLACK)) { // Siyah renk kontrolü
                        blackLinesToRemove.add(line);
                    }
                }
            }
            sol_vbox_anchor.getChildren().removeAll(blackLinesToRemove);

            for (ImageView neighbor : neighbors.keySet()) {
                int distance = calculateDistance(node, neighbor);
                neighbors.put(neighbor, distance); // Butun komsular icin mesafeyi gunceller
                graph.get(neighbor).put(node, distance); // graph da distance ları gunceller
            }

            System.out.println("-------------------------------------------------------------------------------");
            // Ayni zamanda konsola bastırır
            Set<String> printedPaths = new HashSet<>();
            for (ImageView startNode : graph.keySet()) {
                for (ImageView endNode : graph.keySet()) {
                    if (startNode != endNode) { // Yolun kendi üzerinden gecmesini engeller
                        String pathKey = startNode.getId() + "-" + endNode.getId(); // Yollar için hersefer bir benzersiz key olusturur
                        if (!printedPaths.contains(pathKey)) {
                            findShortestPath(null); // Gereksiz düğmeye basma kontrolünden kaçınmak için null değeri parametre girilir.
                            List<ImageView> shortestPath = dijkstra(startNode, endNode);
                            if (shortestPath != null) {
                                int pathLength = 0;
                                for (int i = 0; i < shortestPath.size() - 1; i++) {
                                    ImageView node1 = shortestPath.get(i);
                                    ImageView node2 = shortestPath.get(i + 1);
                                    pathLength += graph.get(node1).get(node2); // Graph dan mesafe alınır
                                }
                                System.out.println(startNode.getId() + " " + pathLength+ " " +endNode.getId()  );

                            } else {
                                System.out.println(startNode.getId() + " " + endNode.getId() + " No Path");
                            }
                            printedPaths.add(pathKey); // ikilemelerden kaçmak için anahtarı ekler
                            printedPaths.add(endNode.getId() + "-" + startNode.getId()); // Yonsuzler için ters cevrilmis ekler.

                        }
                    }
                }
            }
        }
    }

    @FXML
    void drawLineButtonClicked(ActionEvent event) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("image_home1", "image_mosque1", "image_restaurant1", "image_school1", "image_wc1", "image_barrier1", "image_barrier21");
        dialog.setTitle("Node Seçimi");
        dialog.setHeaderText("İki node arasında bir çizgi çizmek için node'lar arasında seçim yapın");
        dialog.setContentText("İlk node'yi seçin:");
        Optional<String> result1 = dialog.showAndWait();

        if (result1.isPresent()) {
            String selectedNode1 = result1.get();
            dialog.setHeaderText("İkinci node'yi seçin:");
            dialog.setContentText("İkinci node'yi seçin:");
            Optional<String> result2 = dialog.showAndWait();

            if (result2.isPresent()) {
                String selectedNode2 = result2.get();
                drawLineBetweenNodes(selectedNode1, selectedNode2);
            }
        }
    }

    private void drawLineBetweenNodes(String nodeName1, String nodeName2) {
        ImageView node1 = nodeNameMap.get(nodeName1);
        ImageView node2 = nodeNameMap.get(nodeName2);

        if (node1 != null && node2 != null) {
            Line line = new Line();
            line.startXProperty().bind(node1.layoutXProperty().add(node1.boundsInLocalProperty().getValue().getWidth() / 2));
            line.startYProperty().bind(node1.layoutYProperty().add(node1.boundsInLocalProperty().getValue().getHeight() / 2));
            line.endXProperty().bind(node2.layoutXProperty().add(node2.boundsInLocalProperty().getValue().getWidth() / 2));
            line.endYProperty().bind(node2.layoutYProperty().add(node2.boundsInLocalProperty().getValue().getHeight() / 2));

            // Mesafe hesaplanır
            int distance = calculateDistance(node1, node2);


            sol_vbox_anchor.getChildren().addAll(line, createDistanceText(distance, line));

            addEdge(node1, node2, distance);


        }
    }
    private Text createDistanceText(int distance, Line line) {
        Text text = new Text(Integer.toString(distance));
        text.setFill(Color.CYAN);


        double centerX = (line.getStartX() + line.getEndX()) / 2;
        double centerY = (line.getStartY() + line.getEndY()) / 2;
        text.setLayoutX(centerX - text.getLayoutBounds().getWidth() / 2);
        text.setLayoutY(centerY - text.getLayoutBounds().getHeight() / 2);

        return text;
    }

    private int calculateDistance(ImageView node1, ImageView node2) {
        double dx = node1.getLayoutX() - node2.getLayoutX();
        double dy = node1.getLayoutY() - node2.getLayoutY();
        return (int) Math.sqrt(dx * dx + dy * dy);
    }
    private void addEdge(ImageView node1, ImageView node2, int distance) {
        /*
        Edge ekleme işlemi için parametre olarak iki node alınır
        bu node lar önce anahtar olark atanır
        ardından birbirlerinin value böölümüne komşusu ve yolu olarak atanır
        Bu şekilde çift yönlü bir edge olur
         */
        if (!graph.containsKey(node1)) {
            graph.put(node1, new LinkedListMap<>());
        }
        if (!graph.containsKey(node2)) {
            graph.put(node2, new LinkedListMap<>());
        }
        graph.get(node1).put(node2, distance);
        graph.get(node2).put(node1, distance); // For undirected graph
    }

    @FXML
    void findShortestPath(ActionEvent event) {
        ImageView startNode = image_home1;
        ImageView endNode = image_restaurant1;
        List<ImageView> shortestPath = dijkstra(startNode, endNode);
        if (shortestPath != null) {
            highlightPath(shortestPath);
        }
    }

    private List<ImageView> dijkstra(ImageView start, ImageView end) {
        //Her düğüm için başlangıç düğümünden o düğüme olan en kısa mesafeyi tutar
        LinkedListMap<ImageView, Integer> distances = new LinkedListMap<>();
        //Her düğüm için o düğüme olan en kısa yolu sağlayan bir önceki düğümü tutar
        //Bu sayede yolu bulabiliriz
        LinkedListMap<ImageView, ImageView> previousNodes = new LinkedListMap<>();

        //İşlenmemiş node ların tutulması için kuyruk oluşturulur.
        Queue<ImageView> queue = new Queue<>();

        for (ImageView node : graph.keySet()) {
            //Node başlangıç nodeuna eşitse uzaklığını 0 yapar.
            if (node == start) {
                distances.put(node, 0);
            } else {
                //Değilse diğer tüm hepsinin uzaklığını sonsuz yapar.
                distances.put(node, Integer.MAX_VALUE);
            }
            //Butun node ları kuyruğa ekler
            queue.add(node);
        }

        //Kuyruk Boş kalana dek -> yani bütün node lar gezilene kadar while döngüsü ile kontrol edilir.
        while (!queue.isEmpty()) {
            ImageView currentNode = null;
            int smallestDistance = Integer.MAX_VALUE;
            /*Kuyruktaki bütün node lar foreach ile gezilir.
              Döngünün başlangıcında başlangıç node u hariç bütün nodeların distance ı 0 olduğu için
              başlangıç nodeunu seçecektir.
             */
            for (ImageView node : queue) {
                if (distances.get(node) < smallestDistance) {
                    smallestDistance = distances.get(node);
                    currentNode = node;
                }
            }
            if (currentNode == null || currentNode == end) {
                break;
            }
            //Seçilen node artık gezilmiş olduğu için kuyruktan çıkarılır
            queue.remove(currentNode);

            /*
            get metodu ile grafın Valuesunu döndük
            Bu value zaten komşu nodeları ve bu komşuların bizim nodeumuza olan uzaklığını
            saklayan bir LinkedListMap olduğu için .

             Ancak bu işlemi her adımda currentNode için yeniden yaparız.

             */
            LinkedListMap<ImageView, Integer> neighbors = graph.get(currentNode);

            if (neighbors != null) {
                //neighbor.keyset burada keylerin yani ImageViewların olduğu liste
                //Neighborda bir Node. Keyi komşu node ve Vlaues u mesafe
                for (ImageView neighbor : neighbors.keySet()) {
                    //neighborun value su mesafe değeridir.
                    int distance = neighbors.get(neighbor);
                    if (!neighbor.getId().equals("image_barrier1") &&
                            !neighbor.getId().equals("image_barrier21")) {
                        //Mevcut mesafe ile topladık ve öncekinden büyükmü diye baktık
                        int newDist = distances.get(currentNode) + distance;
                        if (newDist < distances.get(neighbor)) {
                            //put metodu mecvcut anahtarın evden olan uzaklığını günceller
                            distances.put(neighbor, newDist);
                            previousNodes.put(neighbor, currentNode);
                        }
                        drawLineBetweenNodes(neighbor.getId(), currentNode.getId());
                    }
                }
            }
        }

        //Yolları en son arrayliste ekleriz. Linkedlist yapısından dolayı ters eklenilmek zorunda kaldı
        List<ImageView> path = new ArrayList<>();
        for (ImageView at = end; at != null; at = previousNodes.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        return path.get(0) == start ? path : null;
    }


    private void highlightPath(List<ImageView> path) {

        // Kırmızı çizgileri kaldır
        List<Line> redLinesToRemove = new ArrayList<>();
        for (Node child : sol_vbox_anchor.getChildren()) {
            if (child instanceof Line) {
                Line line = (Line) child;
                if (line.getStroke().equals(Color.RED)) { // Kırmızı renk kontrolü
                    redLinesToRemove.add(line);
                }
            }
        }
        sol_vbox_anchor.getChildren().removeAll(redLinesToRemove);


        // Yol düğümlerini vurgulayın (kırmızı gölge)
        for (ImageView node : path) {
            node.setStyle("-fx-effect: dropshadow(gaussian, red, 10, 0.5, 0, 0)");
        }

        // Mevcut en kısa yol için kırmızı çizgiler
        for (int i = 0; i < path.size() - 1; i++) {
            ImageView node1 = path.get(i);
            ImageView node2 = path.get(i + 1);
            Line line = new Line();
            line.setStartX(node1.getLayoutX() + node1.getBoundsInLocal().getWidth() / 2);
            line.setStartY(node1.getLayoutY() + node1.getBoundsInLocal().getHeight() / 2);
            line.setEndX(node2.getLayoutX() + node2.getBoundsInLocal().getWidth() / 2);
            line.setEndY(node2.getLayoutY() + node2.getBoundsInLocal().getHeight() / 2);
            line.setStyle("-fx-stroke: Red; -fx-stroke-width: 4;");
            sol_vbox_anchor.getChildren().add(line);
        }

    }



}