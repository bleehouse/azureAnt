azureAnt
========

이 프로젝트는 Windows Azure blob 저장소 작업을 위한 두 가지 ANT 작업을 추가하는 간단한 ANT 플러그인입니다. 이 플러그인은 Java 17과 최신 프로그래밍 방식으로 구축되었습니다.

요구사항:
- Java 17 이상
- Apache Ant
- Azure Storage SDK 12.24.0 이상
- Azure Core SDK 1.40.0 이상

azurefileup - Windows Azure blob 저장소에 파일을 업로드하는 작업 

그리고 
azurefiledown - Windows Azure blob 저장소에서 파일을 다운로드하는 작업. 

ANT 작업 jar 파일을 컴파일하려면 프로젝트의 루트 폴더에서 "ant jar" 명령을 실행하세요.

코드 특징:
========
- Java 17 언어 기능 사용
- 최신 컬렉션 프레임워크 사용 (Vector 대신 ArrayList)
- 향상된 switch 표현식
- var 키워드를 통한 타입 추론
- 최신 Azure Storage SDK 호환성

사용법:
========
ANT 스크립트에서 새로운 ANT 작업을 사용하려면:

1. azureant.jar 파일을 ANT 클래스패스에 추가하세요. 예시: 

    <path id="java.myproject.classpath">
        <pathelement location="${build.classes}"/>
        <fileset dir="somefolder">
        	<include name="azureant.jar" />
        </fileset>    	
    </path>
    
2. ANT 스크립트에서 "azurefiledown"과 "azurefileup" 작업을 정의하세요:

<taskdef name="azurefileup" classname="org.citybot.ant.AzureBlobFileUpload" >
     	<classpath>
			<path refid="java.myproject.classpath" />
		</classpath>
</taskdef>

<taskdef name="azurefiledown" classname="org.citybot.ant.AzureBlobFileDownload" >
       	<classpath>
			<path refid="java.myproject.classpath" />
		</classpath>
</taskdef>   

3. Windows Azure 자격 증명을 저장할 ANT 속성을 선언하세요. 예시:

	<property name="azure.key" value="당신의azure키인매우긴문자열" />
	<property name="azure.account" value="azureant" />
	<property name="azure.container" value="testfiles" />
	

4. ANT 대상에서 작업을 사용하세요. 예시: 
Windows Azure blob 저장소에서 파일을 다운로드하려면:

<azurefiledown file="testfiles/azureanttest_download.txt" blob="azureanttest.txt" 
container="${azure.container}" account="${azure.account}" key="${azure.key}"/>

Windows Azure blob 저장소에 파일을 업로드하려면:

<azurefileup container="${azure.container}" list="true" create="true" account="${azure.account}" key="${azure.key}">
    <fileset dir="${env.HOME}/업로드할파일들" includes="*" />
</azurefileup>


5. 작업 매개변수:

    공통 매개변수 (azurefileup과 azurefiledown 작업 모두)
        
        account - 문자열, 필수. Windows Azure Storage 계정 이름. Azure Manager 화면 하단의 "액세스 키 관리"를 클릭하여 Windows Azure 컨테이너의 속성에서 관리할 수 있습니다.
        
        key - 문자열, 필수. Windows Azure Storage 계정 액세스 키. Azure Manager 화면 하단의 "액세스 키 관리"를 클릭하여 Windows Azure 컨테이너의 속성에서 관리할 수 있습니다.
    
    azurefileup:
    
        container - 문자열, 필수. 파일을 업로드할 blob 컨테이너의 이름.
        
        list - 불리언, 선택사항. "true"로 설정하면 업로드가 완료된 후 새 파일이 업로드된 blob 컨테이너의 키 목록을 출력합니다. 기본값은 "false"입니다.
        
        create - 불리언, 선택사항. "container" 매개변수에 지정된 blob 컨테이너가 존재하지 않을 때, 작업이 컨테이너를 생성할 수 있습니다. 
        "create"가 "true"로 설정되면 작업이 최신 Azure SDK 메서드를 사용하여 blob 컨테이너를 생성하려고 시도합니다. "create"가 "false"로 설정되고 컨테이너가 존재하지 않으면 
        작업이 실패합니다. 기본값은 "true"입니다.
        
        <fileset> - FileSet, 필수. 업로드할 파일 목록을 정의하는 Ant <fileset> 요소. 자세한 내용은 다음을 참조하세요: http://ant.apache.org/manual/Types/fileset.html
        
    azurefiledown:
    
        container - 문자열, 필수. 다운로드하려는 blob가 있는 blob 컨테이너의 이름.
        
        blob - 문자열, 필수. 다운로드하려는 blob의 이름.
        
        file - 문자열, 필수. 다운로드한 blob를 저장할 로컬 경로 (파일 이름 포함).