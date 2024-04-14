import extensions.File;
import extensions.CSVFile;
class Mathemon extends Program{

    // sert a limiter le temps lors du tour de joueur (present dans la fonction verifierReponse())
    final double TEMPS_LIMITE=5;

    // sert a initialisé les stat du personnage (utilisé dans la fonction newPerso())
    final int VIE=100;
    final int FORCE=10;
    final int NIVEAU=1;
    final int EXPERIENCE=0;

    // sert a initialisé l'inventaire du personnage (utilisé dans la fonction newInventaire())
    final int POTION=0;
    final int SUPER_POTION=0;
    final int HYPER_POTION=0;
    final int ATTAQUE=0;
    final int MULTI_EXP=0;

    // sert a délimiter le nombre de niveau necessaire pour débloquer un lieu et un outil (utilisé dans la fonction outilsDebloquer() et lieuDebloquer())
    final int NV_DEBLOCAGE=2;

    // sert a donner le % de chance d'obtenir un objet, le chiffre représente le % de chance (utilisé dans la fonction gagnerItem())
    final double PROBA_POTION=0.08;
    final double PROBA_SUPER_POTION=PROBA_POTION + 0.05;
    final double PROBA_HYPER_POTION=PROBA_SUPER_POTION + 0.02;
    final double PROBA_ATTAQUE=PROBA_HYPER_POTION + 0.05;
    final double PROBA_EXP=PROBA_ATTAQUE + 0.05;

    // sert a donner l'intervalle de chiffre qui composeront l'équation donné au joueur, entre 0 et l'intervalle donné (utilisé dans la fonction )
    final int INTERVALLE=9;
    
    // L'algorithm principale

    void algorithm(){
        VerifierFichier();
        CSVFile sauvegarde;
        ////////////////// MENU DU JEU ///////////////////////
        boolean fini=false;
        boolean passe=false;
        Personnage joueur = new Personnage();
        
        int charge;
        while(!passe){
            do{
                afficherMenu();
                do{   
                    charge=stringInt(readString());;
                }while(charge!=1 && charge!=2);
                
                if(charge==1){
                    joueur = newPerso(joueur);
                    RecupererOutils(joueur);
                    RecupererLieux(joueur);
                    passe=true;
                }else{
                    charge=afficherChargerSauvegarde();
                    if(charge!=0){
                        sauvegarde=ChargerSauvegarde(charge);
                        joueur = chargerSaveGame(sauvegarde);
                        RecupererOutils(joueur);
                        RecupererLieux(joueur);
                        passe=true;
                    }
                }
            }while(!passe);
        }
        
        ///////////////// INITIALISÉ LE JEU ///////////////////
        passe=false;
        int choix;
        Ennemie ennemie;
        Ennemie[] listeEnnemie;
        while(!fini){
            choix=afficherInterface();
            if(choix==1){
                afficherStat(joueur);
            }else if(choix==2){
                descriptionObjet(joueur);
            }else if(choix==3){
                choix=afficherMap(joueur.lieux);
                if(choix!=0){
                    listeEnnemie=newEnnemie(choix-1);
                    ennemie = listeEnnemie[(int) (random()*length(listeEnnemie))];
                    commencerCombat(joueur,ennemie, choix-1);
                }
            }else if(choix==4){
                choix=afficherSauvegarde();
                if(choix!=0){
                    fini=true;
                    Sauvegarder(joueur,choix);
                }
            }else if(choix==5){
                fini=true;
            }else if (choix == 6){
                aide();
            }
        }  
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// FONCTION INITIALISATION//////////////////////////// FONCTION INITIALISATION/////////////////////////// FONCTION INITIALISATION/////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Sert a crée un nouveau personnage avec des stat de base

    Personnage newPerso(Personnage p){
        println("Comment vous appelez vous? "+"\n");
        String nom=readString();
        p.nom=nom;
        p.vie=VIE;
        p.vie_max=VIE;
        p.force=FORCE;
        p.niveau=NIVEAU;
        p.experience=EXPERIENCE;
        p.inventaire=newInventaire();
        p.outils=newOutils();
        p.lieux=newLieux();
        return p;
    }

    // sert a donner les outils de base lors de la création d'un personnage (les outils sont les symbolles : + - * / ....)

    Outils newOutils(){
        Outils o =new Outils();
        o.listeOutils=new boolean[]{true,false,false,false,false,false,false};
        return o;
    }

    // sert a donner les lieux de base lors de la création d'un personnage

    Lieux newLieux(){
        Lieu l1=new Lieu();
        l1.visiter=true;
        l1.nom="forêt";
        Lieu l2=new Lieu();
        l2.visiter=false;
        l2.nom="ville";
        Lieu l3=new Lieu();
        l3.visiter=false;
        l3.nom="jungle";
        Lieu l4=new Lieu();
        l4.visiter=false;
        l4.nom="desert";
        Lieu l5=new Lieu();
        l5.visiter=false;
        l5.nom="ocean";
        Lieu l6=new Lieu();
        l6.visiter=false;
        l6.nom="montagne";
        Lieu l7=new Lieu();
        l7.visiter=false;
        l7.nom="prairie";
        Lieux l=new Lieux();
        l.listeLieux=new Lieu[]{l1,l2,l3,l4,l5,l6,l7};
        return l;
    }

    // sert a crée un nouveau inventaire

    Item[] newInventaire(){
        Item potion = new Item();
        potion.nom = "Potion";
        potion.nbItem = POTION;
        potion.description = "Permet de gagner 20 pv.";
        Item superPotion = new Item();
        superPotion.nom = "Super Potion";
        superPotion.nbItem = SUPER_POTION;
        superPotion.description = "Permet de gagner 50 pv.";
        Item hyperPotion = new Item();
        hyperPotion.nom = "Hyper Potion";
        hyperPotion.nbItem = HYPER_POTION;
        hyperPotion.description = "Permet de gagner 200 pv.";
        Item attaquePlus = new Item();
        attaquePlus.nom = "Attaque +";
        attaquePlus.nbItem = ATTAQUE;
        attaquePlus.description = "Double la force jusqu'à la fin du combat en cours.";
        Item multiExp = new Item();
        multiExp.nom = "Multi Exp";
        multiExp.nbItem = MULTI_EXP;
        multiExp.description = "Permet de doubler l'expérience gagnée à la fin du combat.";
        Item[] inventaire = new Item[]{potion, superPotion, hyperPotion, attaquePlus, multiExp};
        return inventaire;
    }

    // sert a crée un nouvel ennemie

    Ennemie[] newEnnemie(int nLieu){
        Ennemie rattata=new Ennemie();
        rattata.nom = "Rattata";
        rattata.vie = 50+20*nLieu;
        rattata.force = 5+5*nLieu;
        rattata.chance = 40+5*nLieu;
        rattata.experience = 20+15*nLieu;
        rattata.file="../ressources/rattata";
        Ennemie salameche = new Ennemie();
        salameche.nom = "Salamèche";
        salameche.vie = 80+20*nLieu;
        salameche.force = 10+5*nLieu;
        salameche.chance = 40+5*nLieu;
        salameche.experience = 50+15*nLieu;
        salameche.file="../ressources/salameche";
        Ennemie carapuce = new Ennemie();
        carapuce.nom = "Carapuce";
        carapuce.vie = 90+20*nLieu;
        carapuce.force = 5+5*nLieu;
        carapuce.chance = 35+5*nLieu;
        carapuce.experience = 50+15*nLieu;
        carapuce.file="../ressources/carapuce";
        Ennemie bulbizarre = new Ennemie();
        bulbizarre.nom = "Bulbizarre";
        bulbizarre.vie = 70+20*nLieu;
        bulbizarre.force = 15+5*nLieu;
        bulbizarre.chance = 45+5*nLieu;
        bulbizarre.experience = 50+15*nLieu;
        bulbizarre.file="../ressources/bulbizarre";
        Ennemie pikachu = new Ennemie();
        pikachu.nom = "Pikachu";
        pikachu.vie = 65+20*nLieu;
        pikachu.force = 10+5*nLieu;
        pikachu.chance = 40+5*nLieu;
        pikachu.experience = 40+15*nLieu;
        pikachu.file="../ressources/pikachu";
        Ennemie evoli = new Ennemie();
        evoli.nom = "Évoli";
        evoli.vie = 60+20*nLieu;
        evoli.force = 5+5*nLieu;
        evoli.chance = 40+5*nLieu;
        evoli.experience = 30+15*nLieu;
        evoli.file="../ressources/evoli";
        Ennemie[] listeEnnemie = new Ennemie[]{rattata, salameche, carapuce, bulbizarre, pikachu, evoli};
        return listeEnnemie;
    }

    void testNewEnnemie(){
        Ennemie[] ennemie=newEnnemie(5);
        assertEquals(160,ennemie[5].vie);
        assertEquals(30,ennemie[5].force);
        assertEquals(65,ennemie[5].chance);
        assertEquals(105,ennemie[5].experience);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// FONCTION AFFICHAGE//////////////////////////// FONCTION AFFICHAGE/////////////////////////// FONCTION AFFICHAGE//////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // sert a afficher l'ecran titre

    void afficherMenu(){
        File f=new File("../ressources/titre_acceuil");
        while(ready(f)){
            println(readLine(f));
        }
    }

    // sert a afficher l'interface (pour choisir quel action faire)

    int afficherInterface(){
        int choix;
        println("\n"+"1. Voir Stat \n"+
                "2. afficher inventaire \n" +
                "3. Voir map \n"+
                "4. Quitter (en sauvegardant)"+"\n"
                +"5. Quitter (sans sauvegarder)"+"\n"
                +"6. Aide");
        do{
            choix=stringInt(readString());;
        }while(choix<1 || choix>6);
        return choix;
    }

    //sert a afficher les stat du personage

    void afficherStat(Personnage joueur){
        println("\n"+"nom : "+joueur.nom +"\n"+"vie : "+joueur.vie+"\n"+"force : "+joueur.force+"\n"+"niveau : "+joueur.niveau+"\n"+"exp : "+joueur.experience+"/"+expRequis(joueur)+"\n");
    }

    // sert a afficher la selection des lieux

    int afficherMap(Lieux lieu){
        println("\n"+"0. Retour"+"\n");
        int choix;
        for(int i=1;i<=length(lieu.listeLieux);i++){
            if(lieu.listeLieux[i-1].visiter){
                println(i+". "+lieu.listeLieux[i-1].nom);
            }else{
                println(i+". ?????????");
            }
        }
        do{
            println("\n"+"Veuillez choisir un lieu déjà débloqué et qui existe");
            choix = stringInt(readString());
        }while(choix>length(lieu.listeLieux) || choix<0 || (choix!=0 && !lieu.listeLieux[choix-1].visiter));
        return choix;
    }

    // affiche les pv de l'ennemie

    void afficherScene(Ennemie ennemie){
        File f=new File(ennemie.file);
        while(ready(f)){
            println(readLine(f));
        }
        println("\n"+"Vous aller affronter un "+ennemie.nom+"\n"+"Il lui reste "+ennemie.vie+" points de vie"+"\n");
    }

    // affiche l'inventaire du joueur

    void afficherInventaire(Personnage joueur){
        String res = "\n";
        for (int cpt = 0 ; cpt < length(joueur.inventaire) ; cpt = cpt + 1){
            res = res + (cpt + 1) + ". " + joueur.inventaire[cpt].nom + " : " + joueur.inventaire[cpt].nbItem + "\n";
        }
        println(res);
    }

    // affiche la description d'un objet
    
    void descriptionObjet(Personnage joueur){
        int choix;
        afficherInventaire(joueur);
        do{
            print("Sélectionnez un objet pour voir sa description : ");
            choix = stringInt(readString());;
        }while (choix<1 || choix>5);
        println(joueur.inventaire[choix-1].nom + " : " + joueur.inventaire[choix-1].description);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// FONCTION COMBAT//////////////////////////// FONCTION COMBAT/////////////////////////// FONCTION COMBAT//////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // sert a afficher le combat et a faire jouer le personnage et l'ennemie

    void commencerCombat(Personnage joueur, Ennemie ennemie, int lieuChoisi){
        afficherScene(ennemie);
        boolean fini=false;
        boolean gagner=false;
        while(!fini){
            tourJoueur(joueur,ennemie, lieuChoisi);
            if(ennemie.vie<=0){
                gagner=true;
                joueur.vie = joueur.vie + 10;
                if (joueur.vie > joueur.vie_max){
                    joueur.vie = joueur.vie_max;
                }
                gagnerExp(joueur, ennemie);
                gagnerItem(joueur);
                fini=true;
            }else{
              tourEnnemie(ennemie,joueur);  
              if(joueur.vie<=0){
                fini=true;
                mort(joueur);
              }
            }
        }
        joueur.force=FORCE;
        afficherFinCombats(gagner);
    }

    // sert a remettre la vie au max et supprime l'exp a la mort

    Personnage mort(Personnage joueur){
        joueur.vie=joueur.vie_max;
        joueur.experience=0;
        return joueur;
    }

    // sert a faire jouer le joueur

    boolean tourJoueur(Personnage joueur, Ennemie ennemie, int lieuChoisi){
        int choix, objet;
        do{
            println("1. combattre \n" + "2. inventaire");
            choix = stringInt(readString());;
        } while (choix<1 || choix>2);
        if (choix == 1){
            int res = donnerEquation(joueur,lieuChoisi);
            return verifierReponse(res,ennemie,joueur);
        }
        else{
            afficherInventaire(joueur);
            do{
                print("Sélectionnez un objet à utiliser : ");
                objet = stringInt(readString());;
            }while (objet<1 || objet>5);
            if (joueur.inventaire[objet-1].nbItem > 0){
                joueur.inventaire[objet-1].nbItem -= 1;
                if (objet == 1){
                    joueur.vie = joueur.vie + 20;
                    if (joueur.vie > joueur.vie_max){
                        joueur.vie = joueur.vie_max;
                    }
                }else if (objet == 2){
                    joueur.vie = joueur.vie + 50;
                    if (joueur.vie > joueur.vie_max){
                        joueur.vie = joueur.vie_max;
                    }
                }else if (objet == 3){
                    joueur.vie = joueur.vie + 200;
                    if (joueur.vie > joueur.vie_max){
                        joueur.vie = joueur.vie_max;
                    }
                }else if (objet == 4){
                    joueur.force = joueur.force * 2;
                }else if (objet == 5){
                    ennemie.experience = ennemie.experience * 2;
                }
            }else{
                println("Vous n'avez pas cet objet dans votre inventaire");
            }
            return true;
        }
         
    }

    //sert a faire jouer l'ennemie

    void tourEnnemie(Ennemie ennemie, Personnage joueur){
        double frappe = random()*100+1;
        if(frappe<ennemie.chance){
            joueur.vie-=ennemie.force;
            println("\n"+"vous avez été touché");
        }else{
            println("\n"+"Il vous a raté");
        }
        println("\n"+"il vous reste "+joueur.vie+" points de vie");
    }

    // sert a afficher la victoire ou défaite du joueur

    void afficherFinCombats(boolean gagner){
        if(gagner){
            println("Bravo vous avez gagné");
        }else{
            println("Dommage vous avez perdu");
        }
    }

    //sert a crée une équation a poser au joueur
    
    int donnerEquation(Personnage joueur, int lieuChoisi){
        int a = (int) (random() * INTERVALLE+1);
        int b = (int) (random() * INTERVALLE+1);
        char outils = outilsAlea(joueur, lieuChoisi);
        int res;
        if(b>a){
            res=b;
            b=a;
            a=res;
        }
        res=0;
        if (outils == '+'){
            res = a + b;
        } else if (outils == '-'){
            res = a - b;
        } else if (outils == '*'){
            res = a * b;
        } else if (outils == '/'){
            if (b==0){
                b+=1;
            }
            res = a / b;
        } else if (outils == '%'){
            if (b==0){
                b+=1;
            }
            res = a % b;
        } else if (outils == '^'){
            res = puissance(a,b);
        }
        print(a + " " + outils + " " + b + " = ");
        return res;
    }

    //sert a verifier la réponse du joueur a l'équation donné

    boolean verifierReponse(double res, Ennemie ennemie, Personnage joueur){
        double time1=getTime();
        double x = (double) stringInt(readString());;
        double time2=getTime();
        double time=(time2-time1)/1000;
        if (x == res && time<TEMPS_LIMITE){
            println("\n"+"Bravo vous avez réussi !");
            ennemie.vie-=joueur.force;
            println("\n"+"Il lui reste "+ennemie.vie+" points de vie"+"\n");
            return true;
        } else {
            if (time<TEMPS_LIMITE){
                println("\n"+"Dommage la répponse correcte était : " + res);
            }
            else{
                println("\n"+"Dommage, vous avez mis trop de temps. La répponse correcte était : " + res);
            }
            
            return false;
        }
    }

    // sert a choisir un outil aléatoire pour l'équation
    
    char outilsAlea(Personnage joueur,int lieuChoisi){
        String outil= "+-*%^";
        return charAt(outil,(int)(random()*(lieuChoisi+1)));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// FONCTION EXP//////////////////////////// FONCTION EXP/////////////////////////// FONCTION EXP//////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // sert a donner l'experience necessaire pour passer au prochain niveau
    
    int expRequis(Personnage joueur){
        int nv = joueur.niveau; 
        double exp = 100;
        for (int cpt = 1 ; cpt < nv ; cpt = cpt + 1){
            exp = exp * 1.2;
        }
        return (int) exp;
    }

    void testExpRequis(){
        Personnage joueur=new Personnage();
        newPerso(joueur);
        joueur.niveau=7;
        assertEquals(298,expRequis(joueur));
        joueur.niveau=5;
        assertEquals(207,expRequis(joueur));
    }

    // sert a faire gagner de l'experience au joueur et le faire passer de niveau

    Personnage gagnerExp(Personnage joueur, Ennemie ennemie){
        joueur.experience = joueur.experience + ennemie.experience;
        while (joueur.experience >= expRequis(joueur)){
            joueur.experience = joueur.experience - expRequis(joueur);
            joueur.niveau = joueur.niveau + 1;
            joueur.vie_max = (int) (joueur.vie_max * 1.2);
            joueur.vie = joueur.vie_max;
            lieuDebloquer(joueur);
            outilsDebloquer(joueur);
        }
        return joueur;
    }

    void testGagnerExp(){
        Personnage joueur=new Personnage();
        newPerso(joueur);
        Ennemie[] ennemie=newEnnemie(2);
        gagnerExp(joueur,ennemie[5]);
        assertEquals(1,joueur.niveau);
        assertEquals(60,joueur.experience);
    }

    // sert a faire gagner un item 

    Personnage gagnerItem(Personnage joueur){
        double alea = random();
        int cpt = 0;
        if (alea < PROBA_POTION){
            cpt = 0;
            joueur.inventaire[cpt].nbItem = joueur.inventaire[cpt].nbItem + 1;
        }else if (alea < PROBA_SUPER_POTION){
            cpt = 1;
            joueur.inventaire[cpt].nbItem = joueur.inventaire[cpt].nbItem + 1;
        }else if (alea < PROBA_HYPER_POTION){
            cpt = 2;
            joueur.inventaire[cpt].nbItem = joueur.inventaire[cpt].nbItem + 1;
        }else if (alea < PROBA_ATTAQUE){
            cpt = 3;
            joueur.inventaire[cpt].nbItem = joueur.inventaire[cpt].nbItem + 1;
        }else if (alea < PROBA_EXP){
            cpt = 4;
            joueur.inventaire[cpt].nbItem = joueur.inventaire[cpt].nbItem + 1;
        }
        if (alea < PROBA_EXP){
                   println("Bravo vous avez obtenu " + joueur.inventaire[cpt].nom + " !");
        }
        return joueur;
    }

    //sert a debloquer un outils si il passe un certain niveau

    void outilsDebloquer(Personnage joueur){
        int n;
        if(joueur.niveau%NV_DEBLOCAGE==0){
            n=joueur.niveau/NV_DEBLOCAGE;
            joueur.outils.listeOutils[n]=true;
        }
    }

    void testOutilsDebloquer(){
        Personnage joueur=new Personnage();
        newPerso(joueur);
        joueur.niveau=6;
        outilsDebloquer(joueur);
        assertEquals(true,joueur.outils.listeOutils[3]);
    }

    //sert a debloquer un lieu si il passe un certain niveau

    void lieuDebloquer(Personnage joueur){
        int n;
        if(joueur.niveau%NV_DEBLOCAGE==0){   
            n=joueur.niveau/NV_DEBLOCAGE;
            joueur.lieux.listeLieux[n].visiter=true;
        }
    }

    void testLieuDebloquer(){
        Personnage joueur=new Personnage();
        newPerso(joueur);
        joueur.niveau=6;
        lieuDebloquer(joueur);
        assertEquals(true,joueur.lieux.listeLieux[3].visiter);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// FONCTION SAUVEGARDE//////////////////////////// FONCTION SAUVEGARDE////////////////////////// FONCTION SAUVEGARDE//////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //sert a vérifier si le fichier  "NomSauvegarde.CSV" où sont stocké les différents noms des différentes sauvegardes existe. si il n'existe pas, il en crée un vide

    void VerifierFichier(){
        String[] fichier=getAllFilesFromCurrentDirectory();
        boolean trouve=false;int i=0;
        while(!trouve && i<length(fichier)){
            if(equals(fichier[i],"NomSauvegarde.CSV")){
                trouve=true;
            }
            i++;
        }
        if(!trouve){
            String[][] jnom=new String[3][1];
            for(int j=0;j<3;j++){
                jnom[j][0]="vide";
            }
            saveCSV(jnom,"./NomSauvegarde.CSV");
        }
    }

    // sert a sauvegarder sa partie dans l'emplacement de sauveagarde se son choix

    int afficherSauvegarde(){
        String nom;
        CSVFile nomSauvegarde = loadCSV("./NomSauvegarde.CSV");
        println("0. Retour");
        for(int i=1;i<4;i++){
            nom=getCell(nomSauvegarde,i-1,0);
            println("Sauvegarde "+i+". "+nom);
        }
        int choix;
        do{
            choix=stringInt(readString());;
        }while(choix<0 || choix>3);
        return choix;
    }

    void Sauvegarder(Personnage joueur, int choix){
        String nom;
        CSVFile nomSauvegarde = loadCSV("./NomSauvegarde.CSV");
        String[][] jnom=new String[3][1];
        for(int i=0;i<3;i++){
            jnom[i][0]=getCell(nomSauvegarde,i,0);
        }
        String[][] content=new String[2][5];
        content[0][0]=""+joueur.nom;
        content[0][1]=""+joueur.vie;
        content[0][2]=""+joueur.force;
        content[0][3]=""+joueur.niveau;
        content[0][4]=""+joueur.experience;
        for(int i=0;i<5;i++){
            content[1][i]=""+joueur.inventaire[i].nbItem;
        }
        jnom[choix-1][0]=joueur.nom;
        nom="./"+joueur.nom+".CSV";
        saveCSV(jnom,"./NomSauvegarde.CSV");
        saveCSV(content,nom);
    }

    // sert a crée un personnage avec les stat de la sauvegarde sélectionné

    Personnage chargerSaveGame(CSVFile sauvegarde){
        Personnage p =new Personnage();
        p.nom=getCell(sauvegarde,0,0);
        p.vie=stringInt(getCell(sauvegarde,0,1));
        p.force=stringInt(getCell(sauvegarde,0,2));
        p.niveau=stringInt(getCell(sauvegarde,0,3));
        p.experience=stringInt(getCell(sauvegarde,0,4));
        p.outils=newOutils();
        p.lieux=newLieux();
        p.inventaire=chargerInventaire(sauvegarde);
        return p;
    }

    Item[] chargerInventaire(CSVFile sauvegarde){
        Item potion = new Item();
        potion.nom = "Potion";
        potion.nbItem = stringInt(getCell(sauvegarde,1,0));
        potion.description = "Permet de gagner 20 pv.";
        Item superPotion = new Item();
        superPotion.nom = "Super Potion";
        superPotion.nbItem = stringInt(getCell(sauvegarde,1,1));
        superPotion.description = "Permet de gagner 50 pv.";
        Item hyperPotion = new Item();
        hyperPotion.nom = "Hyper Potion";
        hyperPotion.nbItem = stringInt(getCell(sauvegarde,1,2));
        hyperPotion.description = "Permet de gagner 200 pv.";
        Item attaquePlus = new Item();
        attaquePlus.nom = "Attaque +";
        attaquePlus.nbItem = stringInt(getCell(sauvegarde,1,3));
        attaquePlus.description = "Double la force jusqu'à la fin du combat en cours.";
        Item multiExp = new Item();
        multiExp.nom = "Multi Exp";
        multiExp.nbItem = stringInt(getCell(sauvegarde,1,4));
        multiExp.description = "Permet de doubler l'expérience gagnée à la fin du combat.";
        Item[] inventaire = new Item[]{potion, superPotion, hyperPotion, attaquePlus, multiExp};
        return inventaire;
    }

    // sert a récuperer un fichier de sauvegarde

    int afficherChargerSauvegarde(){
        CSVFile nomSauvegarde = loadCSV("./NomSauvegarde.CSV");
        String nom;
        println("0. retour");
        for(int i=1;i<4;i++){
            nom=getCell(nomSauvegarde,i-1,0);
            println("Sauvegarde "+i+". "+nom);
        }
        int choix;
        do{
            do{
                choix=stringInt(readString());;
            }while(choix<0 || choix>3);
            if(choix!=0){
                nom=getCell(nomSauvegarde,choix-1,0);
            }else{
                nom="B";
            }
        }while(equals(nom,"null") || equals(nom,"vide"));
        return choix;
    }

    CSVFile ChargerSauvegarde(int indice){
        CSVFile nomSauvegarde = loadCSV("./NomSauvegarde.CSV");
        String nom;
        nom=getCell(nomSauvegarde,indice-1,0);
        nom="./"+nom+".CSV";
        return loadCSV(nom);
    }

    // sert a recuperer les outils lors du chargement du personnage

    void RecupererOutils(Personnage p){
        int i=-1;
        while(i<p.niveau/NV_DEBLOCAGE && i<length(p.outils.listeOutils)){
            p.outils.listeOutils[i+1]=true;
            i++;
        }
    }

    void testRecupererOutils(){
        Personnage joueur = new Personnage();
        Personnage test = new Personnage();
        newPerso(joueur);
        newPerso(test);
        test.niveau=6;
        joueur.niveau=6;
        joueur.outils.listeOutils[0]=true;
        joueur.outils.listeOutils[1]=true;
        joueur.outils.listeOutils[2]=true;
        joueur.outils.listeOutils[3]=true;
        joueur.outils.listeOutils[4]=false;
        RecupererOutils(test);
        assertEquals(joueur.outils.listeOutils[0],test.outils.listeOutils[0]);
        assertEquals(joueur.outils.listeOutils[1],test.outils.listeOutils[1]);
        assertEquals(joueur.outils.listeOutils[2],test.outils.listeOutils[2]);
        assertEquals(joueur.outils.listeOutils[3],test.outils.listeOutils[3]);
        assertEquals(joueur.outils.listeOutils[4],test.outils.listeOutils[4]);
    }

    // sert a recuperer les lieux lors du chargement du personnage

    void RecupererLieux(Personnage p){
        int i=-1;
        while(i<p.niveau/NV_DEBLOCAGE && i<length(p.lieux.listeLieux)){
            p.lieux.listeLieux[i+1].visiter=true;
            i++;
        }
    }

    void testRecupererLieux(){
        Personnage joueur = new Personnage();
        Personnage test = new Personnage();
        newPerso(joueur);
        newPerso(test);
        test.niveau=6;
        joueur.niveau=6;
        joueur.lieux.listeLieux[0].visiter=true;
        joueur.lieux.listeLieux[1].visiter=true;
        joueur.lieux.listeLieux[2].visiter=true;
        joueur.lieux.listeLieux[3].visiter=true;
        joueur.lieux.listeLieux[4].visiter=false;
        joueur.lieux.listeLieux[5].visiter=false;
        joueur.lieux.listeLieux[6].visiter=false;
        RecupererLieux(test);
        assertEquals(joueur.lieux.listeLieux[0].visiter,test.lieux.listeLieux[0].visiter);
        assertEquals(joueur.lieux.listeLieux[1].visiter,test.lieux.listeLieux[1].visiter);
        assertEquals(joueur.lieux.listeLieux[2].visiter,test.lieux.listeLieux[2].visiter);
        assertEquals(joueur.lieux.listeLieux[3].visiter,test.lieux.listeLieux[3].visiter);
        assertEquals(joueur.lieux.listeLieux[4].visiter,test.lieux.listeLieux[4].visiter);
        assertEquals(joueur.lieux.listeLieux[5].visiter,test.lieux.listeLieux[5].visiter);
        assertEquals(joueur.lieux.listeLieux[6].visiter,test.lieux.listeLieux[6].visiter);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// FONCTION AIDE//////////////////////////// FONCTION AIDE////////////////////////// FONCTION AIDE//////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // sert a afficher la section aide pour donner des informations au joueur.

    void aide(){
        int choix;
        File f;
        do{
            println();
            println("Vous êtes dans la section aide, choisissez une section et nous donnerons des explications");
            println();
            println("0. Retour"
                    +"\n"+"1. Principe du jeu"
                    +"\n"+"2. Combat"
                    +"\n"+"3. Monté de niveau"
                    +"\n"+"4. Les Lieux");
            choix=stringInt(readString());;
            if(choix==1){
                f=new File("../ressources/PrincipeJeu");
                while(ready(f)){
                    println(readLine(f));
                }
            }else if(choix==2){
                f=new File("../ressources/Combat");
                while(ready(f)){
                    println(readLine(f));
                }
            }else if (choix==3){
                f=new File("../ressources/Niveau");
                while(ready(f)){
                    println(readLine(f));
                }
            }else if (choix==4){
                f=new File("../ressources/Lieux");
                while(ready(f)){
                    println(readLine(f));
                }
            }
        }while(choix!=0);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// FONCTION AUTRE//////////////////////////// FONCTION AUTRE////////////////////////// FONCTION AUTRE//////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // sert a changer des chiffres écrit en chaine de caractére en chiffre int

    int stringInt(String texte){
        int nb=0;
        if (charAt(texte,0)=='-'){
            return 0-stringInt(substring(texte,1,length(texte)));
        }
        for (int i=0;i<length(texte);i++){
            nb+=(charAt(texte,i)-'0')*puissance(10,length(texte)-i-1);
        }
        return nb;
    }

    void testStringInt(){
        assertEquals(-85,stringInt("-85"));
        assertEquals(-5,stringInt("-5"));
    }

    // sert a faire une puissance

    int puissance(int nb, int exposant){
        int reponse=nb;
        if(exposant==0){
            return 1;
        }
        for(int i=1;i<exposant;i++){
            reponse*=nb;
        }
        return reponse;
    }

    void testPuissance(){
        assertEquals(25,puissance(5,2));
        assertEquals(125,puissance(5,3));
        assertEquals(81,puissance(3,4));
    }   
}