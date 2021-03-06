package main.java;


import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.scene.media.*;

import java.lang.reflect.Array;
import java.math.*;
import javafx.scene.paint.*;
import javafx.scene.Node;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.util.converter.*;
import javafx.util.*;
import java.util.stream.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



public class Helper{
  
  
  //Metodi jonka avulla voi helposti lukea useita kuvatiedostoja kerralla
  public static ArrayList<Image> getSpritesFromFolder(String folderPath, String fileNameStart, int fileAmount, String fileType)  {
    
    ArrayList<Image> fileList = new ArrayList<Image>();
    		
    for (int number : IntStream.rangeClosed(1, fileAmount).limit(fileAmount).toArray()){
      
      String filePath = folderPath + "/" + fileNameStart + number + fileType;
      fileList.add(new Image(filePath));
      System.out.println(filePath);
      }
   
    return fileList;
  }
  
  //Metodi joka muuttaa kuvan teksturoiduksi suorakulmioksi. K�ytet��n erityisesti pelaajan ja vihollisen tapauksessa
    
  public static Rectangle spriteFromImage(Image image) {
        
       ImagePattern texture = new ImagePattern( image, -0.25,-0.1,1.5,1.5,true);
        
       Rectangle rect = new Rectangle(400 - 30, 400 - 45, 60, 90);
       rect.setFill(texture);
        
       return rect;
     
  }
    
    
  //Metodi joka muuttaa mink� tahansa kuvan teksturoiduksi suorakulmioksi
  public static Rectangle anySpriteFromImage(String imagePath, Pair<Double, Double> location, Double spriteWidth, Double spriteHeight){
    
	  Image image = new javafx.scene.image.Image(imagePath);
    
      ImagePattern texture = new ImagePattern( image, 1.0,1.0,1.0,1.0,true);
        
      Rectangle rect = new Rectangle(location.getKey(), location.getValue(), spriteWidth, spriteHeight);
      rect.setFill(texture);
      return rect;
   
  }
  
  public static <T extends Node, U extends Transform> T transformToNode(T thing, ArrayList<U> transform) {
    
    thing.getTransforms().addAll(transform);
    return thing;
    
  }
  
  //Metodi joka mahdollistaa helpon ��nitiedostojen k�yt�n
  public static ArrayList<AudioClip> getAudioFromFolder(String folderPath, String fileNameStart, int fileAmount, String fileType) {
    
    ArrayList<AudioClip> fileList = new ArrayList<AudioClip>();
    
    for (int number : IntStream.rangeClosed(1, fileAmount).limit(fileAmount).toArray()){
      
      String filePath = folderPath + "/" + fileNameStart + number + fileType;
      fileList.add(new AudioClip(filePath));
      System.out.println(filePath);
      
      }
   
    return fileList;
  }

 //Apumetodi et�isyyksien laskemiseen. Palauttaa suoraviivaisen et�isyyden.
 public static Double absoluteDistance(Pair<Double, Double> a, Pair<Double, Double> b) {
    
    Double xDiff = Math.abs(a.getKey() - b.getKey());
    Double yDiff = Math.abs(a.getValue() - b.getValue());
    
    if(xDiff == 0 && yDiff>0) {return yDiff;}
    else if (yDiff == 0 && xDiff>0) {return xDiff;}
    else if (xDiff>=1 && yDiff>=1) {return Math.sqrt(xDiff*xDiff+yDiff*yDiff);}
    else {return Math.sqrt(xDiff*xDiff+yDiff*yDiff);}
  }
 
  //Apumetodi et�isyyksien laskemiseen. Erottelee x ja y akselit
  public static Pair<Double, Double> axisDistance(Pair<Double, Double> a, Pair<Double, Double> b)  {
    Double xDiff = Math.abs(a.getKey() - b.getKey());
    Double yDiff = Math.abs(a.getValue() - b.getValue());
    Pair<Double, Double> pair = new Pair<Double, Double>(xDiff, yDiff);
    return pair;
  } 
}
//#################################################################################################################################################################################

abstract class UsesGameSprite{
  
  Boolean useMirror = false;
  public Optional<Pair<Double, Double>> locationForSprite;
  public Game game;
  public String lookDirectionForSprite;
  
}

abstract class UsesAnimatedGameSprite extends UsesGameSprite{
	
	 public Optional<Pair<Double, Double>> locationForSprite;
	 public Game game;
	 public String lookDirectionForSprite;
 
     public Boolean isMovingForSprite;
  
}
//GameSprite-luokka yksinkertaistaa pelin olioiden kuvien laadintaa, muokkaamista ja liikuttamista
//K�ytet��n aluksi esineiden kanssa ja my�hemmin jos aikaa riitt�� muuallakin
//Kuva peliss� saa automaattisesti k�ytt�j�ns� sijainnin joten sit� ei tarvitse grafiikkakomponentissa erikseen p�ivitt��

class GameSprite {
  
  //Parametrit
	String imagePath;
	Optional<Pair<Double, Double>> imageStartLocation;
	Pair<Double, Double> imageDimensions;
	UsesGameSprite user;
    Pair<Double, Double> locationOffset;
    Optional<Pair<Double, Double>> overrideLocation;
	
	
	
	
	
 public Double spriteWidth = imageDimensions.getKey();
 public Double spriteHeight = imageDimensions.getValue();
  
 private ImagePattern texture = new ImagePattern(new javafx.scene.image.Image(imagePath), 0,0,1,1,true);
 private ArrayList<Transform> transforms = new ArrayList<Transform>();

  
  
 public Rectangle normalImage() {
  
  if(this.overrideLocation.isPresent()) {
    
  Rectangle rect = new Rectangle(this.overrideLocation.get().getKey() + locationOffset.getKey(), this.overrideLocation.get().getValue() + locationOffset.getValue(), spriteWidth, spriteHeight);
  rect.setFill(texture);
  return rect;
  
  
 }else { 
   
  Rectangle rect = new Rectangle(user.locationForSprite.get().getKey() + locationOffset.getKey(), user.locationForSprite.get().getValue() + locationOffset.getValue(), spriteWidth, spriteHeight);
  rect.setFill(texture);
  return rect;
  }
 }
 
 
 private Rectangle mirrorImage() {
   Rectangle img = normalImage();
   Helper.transformToNode(img, this.mirrorRotate());
   return img;
 }
 
 public Rectangle image() {
	 
	 if(this.user.lookDirectionForSprite == "east") {
		 return this.normalImage();
	 }else {
		 return this.mirrorImage();
	 }

 }
 
 
 public void changeSize(Pair<Double, Double> newDimensions) {
   this.spriteWidth = newDimensions.getKey();
   this.spriteHeight= newDimensions.getValue();
   }
 
 public void rotate(Double amount, Pair<Double, Double> pivot) {
   
   this.image().getTransforms().add(new Rotate(amount, pivot.getKey(), pivot.getValue()));
   
 }
 
 private Pair<Double, Double> userSpriteLocation() {
	 
	 if(user.locationForSprite.isPresent()) {
		 return user.locationForSprite.get();
	 }else {
		 Pair<Double, Double> done = new Pair<Double, Double>(0.0, 0.0);
		 return done;
	 } 
 }
 

 
  private ArrayList<Rotate> mirrorRotate() {
	 ArrayList<Rotate> done = new ArrayList<Rotate>();
	 done.add(new Rotate(180.0, userSpriteLocation().getKey(), userSpriteLocation().getValue() , 0, Rotate.Y_AXIS));
	 return done;
  }
  
  
  
  //Konstruktori luokalle
  public GameSprite(String imagePath, Optional<Pair<Double, Double>> imageStartLocation, Pair<Double, Double >imageDimensions, UsesGameSprite user, Pair<Double, Double> locationOffset, Optional<Pair<Double, Double>> overrideLocation) {
		  
		  this.imagePath = imagePath;
		  this.imageStartLocation = imageStartLocation;
		  this.imageDimensions = imageDimensions;
		  this.user = user;
		  this.locationOffset = locationOffset;
		  this.overrideLocation = overrideLocation;
		  
  }
}

//##########################################################################################################################################################################################################

class AnimatedGameSprite{
  
	
	
	String imageFolderPath;
	String fileNameStart;
	int fileAmount;
	String fileType;
	Optional<Pair<Double, Double>> imageStartLocation;
	Pair<Double, Double> imageDimensions;
	UsesAnimatedGameSprite user;
	Pair<Double, Double> locationOffset;
	Boolean isAlwaysMoving;
	
	
  private ArrayList<Image> images = Helper.getSpritesFromFolder(imageFolderPath, fileNameStart, fileAmount, fileType);
  private List<ImagePattern> textures = images.stream().map( img -> new ImagePattern(img, 0,0,1,1,true)).collect(Collectors.toList());
 

  private int time = 0;
  private int spriteIndex = 0;
  public Double spriteWidth = imageDimensions.getKey();
  public Double spriteHeight = imageDimensions.getValue();
  
  private void updateCurrentSpriteNumber() {
    
    if (this.time % 5 == 0 && spriteIndex < textures.size()-1 && (this.user.isMovingForSprite || this.isAlwaysMoving)) {
      
      spriteIndex += 1;
     
      
    }else if ((this.time % 5 == 0 && spriteIndex == textures.size()-1) || (!this.user.isMovingForSprite && !this.isAlwaysMoving)){
      
      spriteIndex = 0;
      
      }
   
    }
  
 private Rectangle normalImage() { 
		  
   Rectangle rect = new Rectangle(user.locationForSprite.get().getKey() + locationOffset.getKey(),user.locationForSprite.get().getValue() + locationOffset.getValue(), spriteWidth, spriteHeight);
   rect.setFill(textures.get(spriteIndex));
   return rect;
  }
  
  
 private Rectangle mirrorImage() {
   
   Rectangle orig = this.normalImage();
   return Helper.transformToNode(orig, this.mirrorRotate());
   
  }
  
  public Rectangle image() {
   
    time += 1 ;
    updateCurrentSpriteNumber();
    
    if(this.user.lookDirectionForSprite == "east") return normalImage();
    else return mirrorImage();
     
  }
 
 
 public void changeSize(Pair<Double, Double> newDimensions) {
   this.spriteWidth = newDimensions.getKey();
   this.spriteHeight= newDimensions.getValue();
   }
 
 private ArrayList<Rotate> mirrorRotate() {
	 ArrayList<Rotate> done = new ArrayList<Rotate>();
	 done.add(new Rotate(180.0, this.user.locationForSprite.orElse(new Pair<Double, Double>(0.0, 0.0)).getKey(), this.user.locationForSprite.orElse(new Pair<Double, Double>(0.0, 0.0)).getValue() , 0, Rotate.Y_AXIS));
	 return done;
  }
  
//Konstruktori luokalle
		  
public AnimatedGameSprite(String imageFolderPath, String fileNameStart, int fileAmount, String fileType, Optional<Pair<Double, Double>> imageStartLocation, Pair<Double, Double> imageDimensions, UsesAnimatedGameSprite user, Pair<Double, Double> locationOffset, Boolean isAlwaysMoving) {
	 
	 this.imageFolderPath = imageFolderPath;
	 this.fileNameStart = fileNameStart;
	 this.fileAmount = fileAmount;
	 this.fileType = fileType;
	 this.imageStartLocation = imageStartLocation;
	 this.imageDimensions = imageDimensions;
	 this.user = user;
	 this.locationOffset = locationOffset;
	 this.isAlwaysMoving = isAlwaysMoving;
			 
	 
	 
 }
  
}

//###########################################################################################################################################################################

//Actoreille saatavilla oleva k��ntyv� k�si. Huolehtii k�den ja aseen k��nn�st�. Toistaiseksi vain pelaajan k�yt�ss�
class RotatingArm{
	
Actor user;
DirectionVector direction;
  
 private GameSprite armImage = new GameSprite("file:src/main/resources/Pictures/MoonmanHand.png", Optional.empty(), new Pair<Double, Double>(40.0, 25.0), user, new Pair<Double, Double>(-5.0, -13.0), Optional.empty());
 private Rotate armRotate = new Rotate(0.0, pivotPoint().getKey(), pivotPoint().getValue(), 400);
  
 private Pair<Double, Double> pivotPoint() {return user.location.locationInImage();}
  
 public Group completeImage() {
   
    armRotate.setAngle( this.direction.angle() * 50);
    armRotate.setPivotX(pivotPoint().getKey());
    armRotate.setPivotY(pivotPoint().getValue());
   
    Group group = switch(user.equippedWeapon) {
    
    case Weapon weapon:
    	 return new Group(armImage.image(), weapon.sprites(2).image);
    	 break;
    case Optional.empty(): 
    	return new Group(armImage.image());
        break;
    
  }
    
    
   switch (user.lookDirectionForSprite) {
      
      case "east":
    	   group.getTransforms().addAll(armRotate);
    	   break;
      default: 
    	  group.getTransforms().addAll(armRotate);
    	  break;
      
      } 
      
      

   return group;
    
  }
 
 //Konstruktori luokalle
 
 public RotatingArm(Actor user, DirectionVector direction) {
	 
	 this.user = user;
	 this.direction = direction;
	 
 }
  
}

//#########################################################################################################################################################################

//Luokka DirectionVector tarjoaa yksinkertaisemman tavan k�sitell� suuntia esim ammusten tapauksessa
class DirectionVector {
  
 public Pair<Double, Double>originalStartPoint;
 public Pair<Double, Double>originalEndPoint;
	
	
  public Double x = originalEndPoint.getKey() - originalStartPoint.getKey();
  public Double y = originalEndPoint.getValue() - originalStartPoint.getValue();
  
  public Boolean isTowardsLeft() { return this.x<0; }
  public Boolean isTowardsRight(){ return this.x>0; }
  
  public Double length () {
    
    if(x != 0 && y != 0) return Math.sqrt(x*x + y*y);
    else if(x==0) return y;
    else return x;
    
  }
  
  public DirectionVector toUnitVect() { //Metodi muuttaa vektorin yksikk�vektoriksi. Uuden vektorin alkupiste on vanhan alkupiste
    
    Double length = this.length();
    
    if(length == 1) return this;
    else{
      this.x = this.x/length;
      this.y = this.y/length;
      return this;
      
    }
  }
  
  public Double angle() {
    return Math.atan(y/x);
  }
    
  public DirectionVector opposite() {
    
   return new DirectionVector(this.originalEndPoint, this.originalStartPoint);
    
    }
  
  public DirectionVector sum(DirectionVector x) {
    
   return new DirectionVector(this.originalStartPoint, x.originalEndPoint);
    
  }
  
  public DirectionVector scalarProduct(Double num){
    
    return new DirectionVector(this.originalStartPoint, new Pair<Double, Double>(this.originalStartPoint.getKey() + num*this.x, this.originalStartPoint.getValue() +num*this.y));
    
    }
  
  public void update(Pair<Double, Double> newStart, Pair<Double, Double> newEnd) {
    
    this.originalStartPoint = newStart;
    this.originalEndPoint = newEnd;
    
    x = originalEndPoint.getKey() - originalStartPoint.getKey();
    y = originalEndPoint.getValue() - originalStartPoint.getValue();
    
  }
  
  public DirectionVector copy() {return new DirectionVector(this.originalStartPoint, this.originalEndPoint); }
		  
		  
  //Konstruktori luokalle
  public DirectionVector(Pair<Double, Double> originalStartPoint, Pair<Double, Double> originalEndPoint) {
	  
	  this.originalStartPoint = originalStartPoint;
	  this.originalEndPoint = originalEndPoint;
	  
	  
  }
  
  
}

//###########################################################################################################################################################################
  
  //Luokan GamePos tarkoitus on helpottaa pelin asioiden sijaintien k�sittely�. Sen avulla pelin varsinaisten koordinaattien ja kuvakoordinaattien v�lill� vaihtelu on helppoa.
  //Luokka otetaan k�ytt��n my�hemmin jos aikaa j��
  
class GamePos{

//Parametrit
 Pair<Double, Double> inGameCoordinates;
 Boolean isCenter;
 
 public Optional<GameCamera> center = GameWindow.gameCamera;

 private Double inGameX = inGameCoordinates.getKey();
 private Double inGameY = inGameCoordinates.getValue();
 private Double playerHeightOffset = (double) -10;
  
 public Pair<Double, Double> locationInGame() { return new Pair<Double,Double>(inGameX, inGameY); } 
 
 //Jos jonkin asian sijainti muuttuu peliss�, sen sijainti muuttuu kuvassa. Pelaaja on poikkeus.
 
 
 public Pair<Double, Double> locationInImage() {
		 
	if(center.isPresent) {
		
		 if (!this.isCenter){return Pair(inGameX-center.location.locationInGame.getKey()+center.location.locationInImage.getKey(), inGameY - center.location.locationInGame.getValue() + center.location.locationInImage.getValue() + playerHeightOffset);}
	     else { return Pair(GameWindow.stage.width.toDouble/2 ,GameWindow.stage.height.toDouble/2);}
		
		
	} else {
		
		return new Pair<Double, Double>(0.0, 0.0);
	
	}	 		 
 }
		 
		 

  
 public void move(Double dx, Double dy) {
   this.inGameX = this.inGameX + dx;
   this.inGameY = this.inGameY + dy;
 }
 
 public void teleport(Pair<Double, Double> newLoc) {
   this.inGameX = newLoc.getKey();
   this.inGameY = newLoc.getValue();
 }
 
 public void zero() {
   this.inGameX = 0.0;
   this.inGameY = 0.0;
   
 }
 
 //Konstruktori luokalle
 
 public GamePos(Pair<Double, Double> inGameCoord, Boolean isCenterOfAll) {
 	this.inGameCoordinates = inGameCoord;
    this.isCenter = isCenterOfAll;
 }
  
}
  