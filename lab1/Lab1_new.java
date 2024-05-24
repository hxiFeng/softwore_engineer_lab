import java.io.*;
import java.util.*;

public class Lab1_new {
    // 构造有向图，键为string，值为Map<String, Integer>，存储邻居节点及权重
    private static Map<String, Map<String, Integer>> graph = new HashMap<>();

    public static void main(String[] args) {
        // try-with-resources 语法，避免资源泄露
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter the file path:");
            String filePath = scanner.nextLine();
            readTextFile(filePath);

            while (true) {
                System.out.println("Choose an option:");
                System.out.println("1. Show directed graph");
                System.out.println("2. Query bridge words");
                System.out.println("3. Generate new text");
                System.out.println("4. Calculate shortest path");
                System.out.println("5. Random walk");
                System.out.println("6. Exit");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        showDirectedGraph();
                        break;
                    case 2:
                        System.out.println("Enter word1:");
                        String word1 = scanner.nextLine();
                        System.out.println("Enter word2:");
                        String word2 = scanner.nextLine();
                        System.out.println(queryBridgeWords(word1, word2));
                        break;
                    case 3:
                        System.out.println("Enter new text:");
                        String newText = scanner.nextLine();
                        System.out.println(generateNewText(newText));
                        break;
                    case 4:
                        System.out.println("Enter word1:");
                        word1 = scanner.nextLine();
                        System.out.println("Enter word2:");
                        word2 = scanner.nextLine();
                        System.out.println(calcShortestPath(word1, word2));
                        break;
                    case 5:
                        System.out.println(randomWalk());
                        break;
                    case 6:
                        return; // Exit the program
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } // Scanner is automatically closed here
    }

    // 读取输入文本，并构造图
    private static void readTextFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String previousWord = null;
            while ((line = reader.readLine()) != null) {
                // 将输入文本分割成小写的字符串集合
                String[] words = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase().split("\\s+");
                for (String word : words) {
                    if (word.isEmpty())
                        continue;
                    if (previousWord != null) {
                        // 若无previousWord键，则新建一个
                        graph.putIfAbsent(previousWord, new HashMap<>());
                        // 更新previousWord邻居节点的权重
                        graph.get(previousWord).put(word, graph.get(previousWord).getOrDefault(word, 0) + 1);// 记录有向边并更新权重
                    }
                    previousWord = word;
                }
            }
        } catch (IOException e) {// 出现异常则打印错误堆栈信息
            e.printStackTrace();
        }
    }

    // 利用Graphviz可视化
    private static void showDirectedGraph() {
        StringBuilder dot = new StringBuilder("digraph G {\n");
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            String from = entry.getKey();
            for (Map.Entry<String, Integer> edge : entry.getValue().entrySet()) {
                String to = edge.getKey();
                int weight = edge.getValue();
                dot.append(String.format("    \"%s\" -> \"%s\" [label=\"%d\"];\n", from, to, weight));
            }
        }
        dot.append("}\n");

        try {
            File dotFile = new File("graph.dot");
            try (FileWriter writer = new FileWriter(dotFile)) {
                writer.write(dot.toString());
            }

            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tpng", "-o", "graph.png", "graph.dot");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            process.waitFor();
            System.out.println("Graph generated as graph.png");

        } catch (IOException | InterruptedException e) {// 异常处理
            e.printStackTrace();
        }
    }

    // 查询桥接词
    private static String queryBridgeWords(String word1, String word2) {
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }

        Set<String> bridgeWords = new HashSet<>();
        Map<String, Integer> neighbors = graph.get(word1);
        for (String neighbor : neighbors.keySet()) {
            // 图中存在，且邻居节点有word2
            if (graph.containsKey(neighbor) && graph.get(neighbor).containsKey(word2)) {
                bridgeWords.add(neighbor);
            }
        }

        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        }

        return "The bridge words from " + word1 + " to " + word2 + " are: " + String.join(", ", bridgeWords);
    }

    // 生成新文本
    private static String generateNewText(String inputText) {
        String[] words = inputText.toLowerCase().split("\\s+");// 格式转换
        StringBuilder newText = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            newText.append(words[i]).append(" ");
            String bridgeWord = queryBridgeWords(words[i], words[i + 1]);
            if (bridgeWord.startsWith("The bridge words")) { // queryBridgeWords()返回格式
                String[] parts = bridgeWord.split(": ");
                String[] bridges = parts[1].split(", ");
                newText.append(bridges[new Random().nextInt(bridges.length)]).append(" ");
            }
        }
        newText.append(words[words.length - 1]);
        return newText.toString();
    }

    // 计算最短路径
    private static List<List<String>> calcShortestPath(String word1, String word2) {
        if (!graph.containsKey(word1)) {
            System.out.println("No " + word1 + " in the graph!");
            return Collections.emptyList();
        }
        if (!graph.containsKey(word2)) {
            System.out.println("No " + word2 + " in the graph!");
            return Collections.emptyList();
        }

        // 优先队列，用于存储节点，按照距离排序
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));
        // 存储每个节点的所有最短路径
        Map<String, List<List<String>>> allShortestPaths = new HashMap<>();
        // 存储从起点到每个节点的最短距离
        Map<String, Integer> distances = new HashMap<>();
        // 存储每个节点的前驱节点
        Map<String, String> previousNodes = new HashMap<>();

        // 初始化
        for (String vertex : graph.keySet()) {
            distances.put(vertex, vertex.equals(word1) ? 0 : Integer.MAX_VALUE);
            previousNodes.put(vertex, null);
            allShortestPaths.put(vertex, new ArrayList<>());
        }

        distances.put(word1, 0);
        queue.add(new Node(word1, 0));
        allShortestPaths.get(word1).add(new ArrayList<>(Collections.singletonList(word1))); // 将起点添加到最短路径列表中

        // 主循环
        while (!queue.isEmpty()) {
            // 队列不为空时取出最短的
            Node current = queue.poll();
            if (current.name.equals(word2)) {
                return allShortestPaths.get(word2); // 返回所有最短路径
            }

            // 得到所有邻居节点
            Map<String, Integer> neighbors = graph.get(current.name);
            if (neighbors == null)
                continue;
            // 遍历当前节点的所有邻居，计算到邻居的距离 newDist。如果该距离小于当前记录的距离，则更新邻居节点的距离，并将其前驱节点设置为当前节点。
            for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
                int newDist = current.distance + neighbor.getValue();
                Integer distanceToNeighbor = distances.get(neighbor.getKey());
                if (distanceToNeighbor == null || newDist < distanceToNeighbor) {
                    distances.put(neighbor.getKey(), newDist);
                    previousNodes.put(neighbor.getKey(), current.name);
                    queue.add(new Node(neighbor.getKey(), newDist));

                    // 更新邻居节点的所有最短路径。
                    List<List<String>> shortestPathsToNeighbor = new ArrayList<>(allShortestPaths.get(current.name));
                    for (List<String> path : shortestPathsToNeighbor) {
                        List<String> newPath = new ArrayList<>(path);
                        newPath.add(neighbor.getKey());
                        allShortestPaths.computeIfAbsent(neighbor.getKey(), k -> new ArrayList<>()).add(newPath);
                    }
                } else if (newDist == distanceToNeighbor) {// 距离相等时，也存储
                    List<List<String>> shortestPathsToNeighbor = new ArrayList<>(allShortestPaths.get(current.name));
                    for (List<String> path : shortestPathsToNeighbor) {
                        List<String> newPath = new ArrayList<>(path);
                        newPath.add(neighbor.getKey());
                        allShortestPaths.computeIfAbsent(neighbor.getKey(), k -> new ArrayList<>()).add(newPath);
                    }
                }
            }
        }

        System.out.println("No path from " + word1 + " to " + word2 + "!");
        return Collections.emptyList();
    }

    // private static List<String> calcShortestPath(String word1, String word2) {
    // if (!graph.containsKey(word1)) {
    // System.out.println("No " + word1 + " in the graph!");
    // return Collections.emptyList();
    // }
    // if (!graph.containsKey(word2)) {
    // System.out.println("No " + word2 + " in the graph!");
    // return Collections.emptyList();
    // }

    // // Dijkstra's algorithm
    // PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(node
    // -> node.distance));
    // Map<String, List<String>> shortestPaths = new HashMap<>();
    // Map<String, Integer> distances = new HashMap<>();
    // Map<String, String> previousNodes = new HashMap<>();

    // for (String vertex : graph.keySet()) {
    // distances.put(vertex, vertex.equals(word1) ? 0 : Integer.MAX_VALUE);
    // previousNodes.put(vertex, null);
    // shortestPaths.put(vertex, new ArrayList<>());
    // }

    // distances.put(word1, 0);
    // queue.add(new Node(word1, 0));
    // shortestPaths.get(word1).add(word1);

    // while (!queue.isEmpty()) {
    // Node current = queue.poll();
    // if (current.name.equals(word2)) {
    // return shortestPaths.get(word2); // 只返回一次最短路径
    // }

    // Map<String, Integer> neighbors = graph.get(current.name);
    // if (neighbors == null)
    // continue;

    // for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
    // int newDist = current.distance + neighbor.getValue();
    // Integer distanceToNeighbor = distances.get(neighbor.getKey());
    // if (distanceToNeighbor == null || newDist < distanceToNeighbor) {
    // distances.put(neighbor.getKey(), newDist);
    // previousNodes.put(neighbor.getKey(), current.name);
    // queue.add(new Node(neighbor.getKey(), newDist));

    // List<String> shortestPathToNeighbor = new
    // ArrayList<>(shortestPaths.get(current.name));
    // shortestPathToNeighbor.add(neighbor.getKey());
    // shortestPaths.put(neighbor.getKey(), shortestPathToNeighbor);
    // }
    // }
    // }

    // System.out.println("No path from " + word1 + " to " + word2 + "!");
    // return Collections.emptyList();
    // }

    // private static String calcShortestPath(String word1, String word2) {
    // if (!graph.containsKey(word1)) {
    // return "No " + word1 + " in the graph!";
    // }
    // if (!graph.containsKey(word2)) {
    // return "No " + word2 + " in the graph!";
    // }

    // // Dijkstra's algorithm
    // PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(node
    // -> node.distance));// 创建一个优先队列
    // // queue，用于存储待处理的节点，优先队列按照节点的距离（distance）进行排序
    // Map<String, Integer> distances = new HashMap<>();
    // Map<String, String> previousNodes = new HashMap<>();

    // for (String vertex : graph.keySet()) {
    // distances.put(vertex, Integer.MAX_VALUE);
    // previousNodes.put(vertex, null);
    // }

    // distances.put(word1, 0);
    // queue.add(new Node(word1, 0));

    // while (!queue.isEmpty()) {
    // Node current = queue.poll();
    // if (current.name.equals(word2))
    // break;

    // Map<String, Integer> neighbors = graph.get(current.name);
    // if (neighbors == null)
    // continue;

    // for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
    // int newDist = current.distance + neighbor.getValue();
    // if (newDist < distances.get(neighbor.getKey())) {
    // distances.put(neighbor.getKey(), newDist);
    // previousNodes.put(neighbor.getKey(), current.name);
    // queue.add(new Node(neighbor.getKey(), newDist));
    // }
    // }
    // }

    // if (distances.get(word2) == Integer.MAX_VALUE) {
    // return "No path from " + word1 + " to " + word2 + "!";
    // }

    // String path = word2;
    // String step = word2;
    // while ((step = previousNodes.get(step)) != null) {
    // path = step + " -> " + path;
    // }
    // return "The shortest path from " + word1 + " to " + word2 + " is: " + path +
    // " with total weight "
    // + distances.get(word2);
    // }
    // 随机游走
    private static String randomWalk() {
        if (graph.isEmpty()) {
            return "Graph is empty!";
        }

        Random random = new Random();
        List<String> keys = new ArrayList<>(graph.keySet());
        // 随机选择一个节点
        String current = keys.get(random.nextInt(keys.size()));
        // walk记录游走路径
        StringBuilder walk = new StringBuilder(current);

        while (graph.containsKey(current) && !graph.get(current).isEmpty()) {
            Map<String, Integer> neighbors = graph.get(current);
            int totalNeighbors = neighbors.size(); // 获取邻居节点的数量
            int rand = random.nextInt(totalNeighbors); // 随机选择一个节点
            int index = 0;
            String selectedNeighbor = null;

            for (String neighbor : neighbors.keySet()) {
                if (index == rand) { // 当索引等于随机数时，选择当前节点
                    selectedNeighbor = neighbor;
                    break;
                }
                index++;
            }

            if (selectedNeighbor != null) {
                current = selectedNeighbor;
                walk.append(" -> ").append(current);
            }

            if (random.nextDouble() < 0.15) { // 15% chance to end the walk
                break;
            }
        }

        return "Random walk: " + walk.toString();
    }

    private static class Node {
        String name;
        int distance;

        Node(String name, int distance) {
            this.name = name;
            this.distance = distance;
        }
    }
}
