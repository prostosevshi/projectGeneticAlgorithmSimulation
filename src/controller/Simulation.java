package controller;

import movingEntity.Creature;
import model.Entity;
import staticEntity.Food;
import staticEntity.Poison;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation {

    private final WorldMap worldMap;
    private int turnCounter = 0;

    public Simulation(int mapWidth, int mapHeight) {
        this.worldMap = new WorldMap(mapWidth, mapHeight);
        initializeMap();
    }

    private void initializeMap() {
        Random random = new Random();

        initializeFoodAndPoison();

        // Добавляем существ
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(worldMap.getWidth());
            int y = random.nextInt(worldMap.getHeight());
            double[] genome = {Math.random(), Math.random(), Math.random()};
            worldMap.addEntity(new Creature(x, y, genome));
        }
    }

    private void nextTurn() {
        turnCounter++;
        System.out.println("Turn " + turnCounter);

        List<Entity> currentEntities = new ArrayList<>(worldMap.getEntities());

        List<Entity> entitiesToRemove = new ArrayList<>();

        // Все существа делают ход
        for (Entity entity : currentEntities) {
            if (entity instanceof Creature creature) {
                moveCreature(creature);
                interact(creature, entitiesToRemove);
            }
        }

        removeDeadCreatures();

        for (Entity entity : entitiesToRemove) {
            worldMap.removeEntity(entity);
        }

        worldMap.render();

        // Проверка на вымирание
        if (allCreaturesDead()) {
            evolveOnExtinction();
        }
    }

    private boolean allCreaturesDead() {
        return worldMap.getEntities().stream().noneMatch(e -> e instanceof Creature);
    }

    private void moveCreature(Creature creature) {
        Random random = new Random();
        int newX = creature.getX() + random.nextInt(3) - 1;
        int newY = creature.getY() + random.nextInt(3) - 1;

        // Проверяем границы карты
        if (newX >= 0 && newX < worldMap.getWidth() && newY >= 0 && newY < worldMap.getHeight()) {
            creature.setPosition(newX, newY);
        }
    }

    private void interact(Creature creature, List<Entity> entitiesToRemove) {
        Entity entity = worldMap.getEntityAt(creature.getX(), creature.getY());
        if (entity instanceof Food) {
            creature.changeHealth(10);
            entitiesToRemove.add(entity);
        } else if (entity instanceof Poison) {
            creature.changeHealth(-10);
            entitiesToRemove.add(entity);
        }
        creature.changeHealth(-1); // Теряет здоровье каждый ход
    }

    private List<Creature> selectTopCreatures(int topN) {
        return worldMap.getEntities().stream()
                .filter(e -> e instanceof Creature)
                .map(e -> (Creature) e)
                .sorted((c1, c2) -> Integer.compare(c2.getHealth(), c1.getHealth()))
                .limit(topN)
                .toList();
    }

    private double[] crossover(double[] genome1, double[] genome2) {
        double[] newGenome = new double[genome1.length];
        for (int i = 0; i < genome1.length; i++) {
            newGenome[i] = Math.random() < 0.5 ? genome1[i] : genome2[i]; // Берём ген от одного из родителей
        }
        return newGenome;
    }

    private double[] mutate(double[] genome, double mutationRate) {
        Random random = new Random();
        for (int i = 0; i < genome.length; i++) {
            if (random.nextDouble() < mutationRate) {
                genome[i] += random.nextGaussian() * 0.1; // Небольшое случайное изменение
            }
        }
        return genome;
    }

    private void evolveOnExtinction() {
        System.out.println("Все существа вымерли. Начинается эволюция!");
        Random random = new Random();

        int newPopulationSize = 5; // Размер новой популяции
        double mutationRate = 0.1;  // Шанс мутации

        // Отбираем лучших из вымершей популяции (если есть выжившие к моменту вызова)
        List<Creature> topCreatures = selectTopCreatures(2);

        // Удаляем все существа и объекты
        worldMap.getEntities().clear();

        // Создаем новое поколение
        for (int i = 0; i < newPopulationSize; i++) {
            double[] genome;
            if (!topCreatures.isEmpty()) {
                // Кроссовер и мутация на основе лучших геномов
                Creature parent1 = topCreatures.get(random.nextInt(topCreatures.size()));
                Creature parent2 = topCreatures.get(random.nextInt(topCreatures.size()));
                genome = mutate(crossover(parent1.getGenome(), parent2.getGenome()), mutationRate);
            } else {
                // Если никого не осталось, создаем случайные геномы
                genome = createRandomGenome();
            }

            int x = random.nextInt(worldMap.getWidth());
            int y = random.nextInt(worldMap.getHeight());
            worldMap.addEntity(new Creature(x, y, genome));
        }

        // Возвращаем еду и яд
        initializeFoodAndPoison();
    }

    private double[] createRandomGenome() {
        Random random = new Random();
        double[] genome = new double[5]; // Размер генома
        for (int i = 0; i < genome.length; i++) {
            genome[i] = random.nextDouble(); // Ген от 0 до 1
        }
        return genome;
    }

    private void initializeFoodAndPoison() {
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            int x = random.nextInt(worldMap.getWidth());
            int y = random.nextInt(worldMap.getHeight());
            worldMap.addEntity(new Food(x, y));
        }

        for (int i = 0; i < 2; i++) {
            int x = random.nextInt(worldMap.getWidth());
            int y = random.nextInt(worldMap.getHeight());
            worldMap.addEntity(new Poison(x, y));
        }
    }

    private void removeDeadCreatures() {
        List<Entity> toRemove = new ArrayList<>();
        for (Entity entity : worldMap.getEntities()) {
            if (entity instanceof Creature creature && creature.getHealth() <= 0) {
                toRemove.add(entity);
            }
        }
        worldMap.getEntities().removeAll(toRemove);
    }

    public void start() {
        System.out.println("Starting simulation...");
        while (true) {
            nextTurn();

            try {
                Thread.sleep(100); // 1000 миллисекунд = 1 секунда
            } catch (InterruptedException e) {
                System.err.println("Simulation interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }

            // Если все существа вымерли, производим эволюцию
            if (allCreaturesDead()) {
                evolveOnExtinction();
            }
        }
    }
}
