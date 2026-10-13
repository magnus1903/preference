* ÖPPNA "Android Studio" OCH REDIGERA DET SOM ÖNSKAS

	- klicka på: "Open in Android Studio"
	
	OBS OBS OBS Gör ingen "commit", detta görs när en ny "tag" skapas (under ”SKAPA release”).

	
* SKAPA en "release"

	Med "GitHub Desktop" kan du skapa annoterade "tags". "Tags" är kopplade till "commits", så du kan använda en "tag"
	för att markera en specifik punkt i "the repository´s" historik, med ett versionsnummer för en "release".

	Förutom att skapa en ny "tag" kommer detta även att "commit" ändringarna och VIKTIGT skapa en aar- och en pom-fil
	som kan refereras till i andra projekt som ett "gradle-beroende" (se projektet Petanque).
	
	- klicka på: "Historik" i det vänstra sidofältet
		
	- högerklicka på: ”Initial commit”
	
		+ i drop-down menyn
	
			# välj: "Create tag..."

	- i popup-dialogen "Create a tag"
	
		+ "Name" = "versionsnumret i build.gradle"
				
		+ klicka på: "Create tag" ("GitHub Desktop" visar en pil om "the tag" inte har skickats till "the remote repository")

	- klicka på: "Changes" i det vänstra sidofältet
		
		+ "Summary (required) (ovanför "Description")" = "versionsnumret i build.gradle"
		
		+ "Description" = "beskrivande text"
			
		+ klicka på: "Commit ... to main"
			
	- klicka på: "Push origin" på huvudsidan i "GitHub Desktop"


* HÄMTA DEN NYA "the release" SOM SKAPATS OVAN TILL "JitPack"

	- öppna: https://jitpack.io/ i webbläsaren
	
	- i "the look" fältet
	
		+ ange: "magnus1903/preference"
	
		+ klicka på: "Look up"
		
			# om inte alla "releases" visas kan sidan behöva laddas om
	
	- vänta på att loggfilen för den nya versionen ("versionsnumret i build.gradle") skapats
	
	- när loggfilen har skapats
	
		+ klicka på: "Get it" för att få "Gradle-information"
		

* FÖR ATT TA BORT ETT REPOSITORY I "GitHub"

	- öppna: https://github.com/ i webbläsaren

	- om du inte är inloggad

		+ klicka på: "Sign in" (längst upp till höger)
		
		+ på "Sign in" sidan
		
			# "Username or email address" = "ditt användarnamn"
		
			# "Password" = "ditt lösenord"
				
			# klicka på: "Sign in"
			
		+ på "Two-factor authentication" sidan
				
			# klicka på: "More options"
			
			# klicka på: "SMS Code"
			
		+ på "Two-factor authentication" sidan (som visas igen med nytt innehåll) 
			
			# klicka på: "Send SMS" vilket skickar ett SMS med koden till telefonen
			
		+ på "Two-factor authentication" sidan (som visas igen med nytt innehåll) 
			
			# ange "SMS-koden" och du loggas in

	- om du är inloggad
			
		+ klicka på: "the repository" som du vill ta bort i det vänstra sidofältet
				
		+ på "the repository" sidan
				
			# klicka på: "Settings" (i "the toolbar")
				
		+ på "the Settings" sidan
				
			# skrolla till: "Danger Zone" (längst ner på sidan)

			# klicka på: "Delete this repository" och följ instruktionerna
		

* FÖR ATT ÄNDRA PÅ "SYNLIGHETEN" PÅ ETT "repository" I "GitHub", VILKET KAN BEHÖVAS OM "JitPack" "klagar"

	- om du inte är inloggad
	
		+ gör som vid "FÖR ATT TA BORT ETT REPOSITORY I "GitHub""

	- om du är inloggad

		+ klicka på: "the repository" som du vill ändra synligheten på i det vänstra sidofältet
				
		+ på "the repository" sidan
				
			# klicka på: "Settings" (i "the toolbar")
				
		+ på "the Settings" sidan
				
			# skrolla till: "Danger Zone" (längst ner på sidan)

			# klicka på: "Change visibility" och följ instruktionerna

		
**************************************************************************************************
*                                                                       						 * 
* TAG ALDRIG BORT:													   							 *
*                                                                       						 * 
*	- "the repository" https://github.com/magnus1903/preference        							 *
*                                                                       						 * 
*                                                                       						 * 
*                                                                       						 * 
* TAG ALDRIG BORT:																				 *
*                                                                       						 * 
*	- mappen D:\Sandbox\AndroidStudioProjects\preference\.git          							 *
*                                                                       						 * 
*	- filen D:\Sandbox\AndroidStudioProjects\preference\.gitattributes 							 *
*                                                                       						 * 
*	- filen D:\Sandbox\AndroidStudioProjects\preference\.gitignore     							 *
*                                                                       						 * 
*                                                                       						 * 
*                                                                       						 * 
* MEN OM NÅGONTING AV DE OVANSTÅENDE "objekten" INTE FINNS:										 *
*                                                                       						 * 
*	- tag bort mappen: D:\Sandbox\AndroidStudioProjects\preference\.git" (om den finns)          *
*                                                                       						 * 
*	- tag bort filen: D:\Sandbox\AndroidStudioProjects\preference\.gitattributes" (om den finns) *
*                                                                       						 * 
*	- tag bort filen: D:\Sandbox\AndroidStudioProjects\preference\.gitignore" (om den finns)     *
*                                                                       						 * 
*	- starta "GitHub Desktop"																	 *
*                                                                       						 * 
*		+ om "Can´t find "preference" "dyker upp" 												 *
*                                                                       						 * 	
*		+ klicka på: "Remove"																	 *
*                                                                       						 * 
*	- på huvudsidan i "GitHub Desktop"															 *
*                                                                       						 * 
*		+ klicka på: "Create a New Repository on your local drive..."							 *
*                                                                       						 * 
*		+ i popup-dialogen "Create a new repository"											 *
*                                                                       						 * 
*				# "Name" = "preference" (d v s exakt samma namn som på projektet)				 *
*                                                                       						 * 
*				# "Local path" = D:\Sandbox\AndroidStudioProjects								 * 
*                                                                       						 * 
*				# "Description" = "a library of custom android preferences"						 *
*                                                                       						 * 
*				# "Initialize this repository with a README" = "unchecked"						 *
*                                                                       						 * 
*				# "Git ignore" = "Android" (i drop-down menyn)          						 *
*                                                                       						 * 
*				# "Licence" = "None" (i drop-down menyn)										 *
*                                                                       						 * 
*				# klicka på: "Create repository"												 *
*                                                                       						 * 
**************************************************************************************************
