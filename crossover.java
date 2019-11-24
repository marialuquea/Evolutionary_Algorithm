private Individual multipoint(Individual parent1, Individual parent2)
    {
        if(Parameters.rnd.nextDouble() > Parameters.crossoverProbability)
            return parent1;

        Individual child = new Individual();

        int pacingCrossoverPoint1 = Parameters.rnd.nextInt(parent1.pacingStrategy.length);
        int pacingCrossoverPoint2 = Parameters.rnd.nextInt(parent1.pacingStrategy.length);

        if(pacingCrossoverPoint1 > pacingCrossoverPoint2)
        {
            int temp = pacingCrossoverPoint1;
            pacingCrossoverPoint1 = pacingCrossoverPoint2;
            pacingCrossoverPoint2 = temp;
        }

        for(int i = 0; i < pacingCrossoverPoint1; i++)
            child.pacingStrategy[i] = parent1.pacingStrategy[i];

        for(int i = pacingCrossoverPoint1; i < pacingCrossoverPoint2; i++)
            child.pacingStrategy[i] = parent2.pacingStrategy[i];

        for(int i = pacingCrossoverPoint2; i < parent1.pacingStrategy.length; i++)
            child.pacingStrategy[i] = parent1.pacingStrategy[i];


        int transitionCrossoverPoint1 = Parameters.rnd.nextInt(parent1.transitionStrategy.length);
        int transitionCrossoverPoint2 = Parameters.rnd.nextInt(parent1.transitionStrategy.length);

        if(transitionCrossoverPoint1 > transitionCrossoverPoint2)
        {
            int temp = transitionCrossoverPoint1;
            transitionCrossoverPoint1 = transitionCrossoverPoint2;
            transitionCrossoverPoint2 = temp;
        }

        for(int i = 0; i < transitionCrossoverPoint1; i++)
            child.transitionStrategy[i] = parent1.transitionStrategy[i];

        for(int i = transitionCrossoverPoint1; i < transitionCrossoverPoint2; i++)
            child.transitionStrategy[i] = parent2.transitionStrategy[i];

        for(int i = transitionCrossoverPoint2; i < parent1.transitionStrategy.length; i++)
            child.transitionStrategy[i] = parent1.transitionStrategy[i];

        return child;
    }
