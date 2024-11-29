package jdev.mentoria.lojavirtual.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.mentoria.lojavirtual.ExceptionMentoriaJava;
import jdev.mentoria.lojavirtual.model.Produto;
import jdev.mentoria.lojavirtual.repository.ProdutoRepository;
import jdev.mentoria.lojavirtual.service.ServiceSendEmail;

@Controller
@RestController
public class ProdutoController {
	
	@Autowired
	private ProdutoRepository  produtoRepository;
	
	@Autowired
	private ServiceSendEmail serviceSendEmail; 
	
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/salvarProduto") /*Mapeando a url para receber JSON  @Valid deve haver uma anotação disso na classe produto do pacote model*/
	public ResponseEntity<Produto> salvarAcesso(@RequestBody @Valid Produto produto) throws ExceptionMentoriaJava, MessagingException, IOException { /*Recebe o JSON e converte pra Objeto*/
		
		if (produto.getTipoUnidade() == null || produto.getTipoUnidade().trim().isEmpty()) {
			throw new ExceptionMentoriaJava("Tipo da unidade deve ser informada");
		}
		
		if (produto.getNome().length() < 10) {
			throw new ExceptionMentoriaJava("Nome do produto deve ter mais de 10 letras.");
		}
		
		if (produto.getEmpresa() == null || produto.getEmpresa().getId() <= 0) {
			throw new ExceptionMentoriaJava("Empresa responsável deve ser informada");
		}
		
		if (produto.getId() == null) {
		  List<Produto> produtos  = produtoRepository.buscarProdutoNome(produto.getNome().toUpperCase(), produto.getEmpresa().getId());
		  
		  if (!produtos.isEmpty()) {
			  throw new ExceptionMentoriaJava("Já existe Produto com a descrição: " + produto.getNome());
		  }
		}	
		
		
		if (produto.getCategoriaProduto() == null || produto.getCategoriaProduto().getId() <= 0) {
			throw new ExceptionMentoriaJava("Categoria deve ser informada");
		}
		
		
		if (produto.getMarcaProduto() == null || produto.getMarcaProduto().getId() <= 0) {
			throw new ExceptionMentoriaJava("Marca deve ser informada");
		}
		
		if (produto.getQtdEstoque() < 1) {
			throw new ExceptionMentoriaJava("O produto dever ter no minímo 1 no estoque.");
		}
		
		if (produto.getImagens() == null || produto.getImagens().isEmpty() || produto.getImagens().size() == 0) {
			throw new ExceptionMentoriaJava("Deve ser informado imagens para o produto.");
		}
		
		if (produto.getImagens().size() < 3) {
			throw new ExceptionMentoriaJava("Deve ser informado pelo menos 3 imagens para o produto.");
		}
		
		
		if (produto.getImagens().size() > 6) {
			throw new ExceptionMentoriaJava("Deve ser informado no máximo 6 imagens.");
		}
		
		//aula 5.15 -46:52
		//Antes de salvar vamos fazer a parte de processar as imagens, se for um produto novo ele não tem um id primary key
		//o que acontece agora?
		//isso aqui vai receber um json que vai vir em um atibutoi de "imagens" e varios objeots de imagens dentro dele
		//o json vem fo endpoint o spring vai converter o esse json para objeto e com isso entro no sistema para nós fizermos o que quisermos
		//só que quando mandamos um json para o produto, os objetos de imagem desta lista private List<ImagemProduto> ImagemProduto que esta 
		//no pacote model produto.java não sabem qual que é o pai, nós só estamos manando a imagem não estamos falando quem que é o pai
		//então temos que manipular esses objeots em memória no java para evitar de dar prolema de chave estrangeira, pois ele vai tentar
		//salvar uma imagem mas não vai estar associado ao produto, então antes de salvar nós temos que pelo menos fazer essa referência
		//em memória para o java... pois para o java cada objeto instanciado ele tem um codigo do objeto em memória, na hora do jpa salvar o spring data
		//hibernate etc... salvar isso no banco, ele vai fazer essa referencia com esse código em memória para associar a chave no banco, então temos que fazer essa associação aqui.
		//e para isso vamos varrer a lista fazendo um for		
		if (produto.getId() == null) {
			//para cada imagem a gente vai associar o objeto de imagem, associar o produto ligar  imagem do produto
			// vamos pegar o produto.getImagens() minha lista de imagens  pegamos o produto     get(x) = objeto na posição do for aqui tenho de retorno um objeto de imagem setproduto(produto) quem é o pai dessa imagem
			for (int x = 0; x < produto.getImagens().size(); x++) {
				produto.getImagens().get(x).setProduto(produto);
				//fazemos tb a referencia a baixo
				produto.getImagens().get(x).setEmpresa(produto.getEmpresa());
				
				//vamos pegar só a parte da imagem para processar
				String base64Image = "";
			
				//vamos validar para não vir a imagem com quebra, primeiro a imagem vem como original depois vamos converte-la em miniatura e gravar a
				//miniatura se contiver essa tag data:image
				if (produto.getImagens().get(x).getImagemOriginal().contains("data:image")) {
					base64Image = produto.getImagens().get(x).getImagemOriginal().split(",")[1]; //split quebramos ela bem na virgula a imagem vem assim: data:image/png:base64 ele vem como um array na posição 0 na posição 1 vamos ter a imagem em si
				}else { //get(x) na posição do for
					//o contrario disso pego a imagem pura sem quebrar ela
					base64Image = produto.getImagens().get(x).getImagemOriginal();
				}
				
				//temos que converter a imagem base 64 para um byte, vamos usar esse objeto DatatypeConverter
				//parseBase64Binary(base64Image) retorna um byte array, esse base64Image estamos passando ele
				//e com isso vou ter um array de byte[] imageBytes
				byte[] imageBytes =  DatatypeConverter.parseBase64Binary(base64Image);
				
				//agente pega esse byte agora e fazemos um buffer, que vai armazenar isso aqui para agente poder processsar a miniatura
				//vamos usar esse obejto ImageIO
				//estamos fazendo a entrada de dados
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
				
				//se ele conseguiu converter
				if (bufferedImage != null) {
					//agora primeira coisa que temos que fazer é estabelecer o tipo da imagem, se ela é png,jpg etc... do jeito que ela entrar
					//vamos ter que salva-la senão vamos quebrar a imagem, esse bufferedImage tem varias propriedades para trabalharmos com imagem
					//cada numero bufferedImage.getType() == 0 é um padrão de imagem aula 5.12 -37:28
					int type = bufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
					//começamos a conversão
					int largura = Integer.parseInt("800");
					int altura = Integer.parseInt("600");
					
					//agora vamos trabalhar com objeto grafico vamos pegar outro buffer de imagem que é o objeto que vamos escrever a nova imagem
					//com o tamanho novo, passamos a alrgura altura e o tipo
					BufferedImage resizedImage = new BufferedImage(largura, altura, type);
					//usamos esse obejto grafico Graphics2D g pegamos o de cima resizedImage
					Graphics2D g = resizedImage.createGraphics();
					//agora vamos escrever... passamos o buffer = bufferedImage a altura largura de novo...
					g.drawImage(bufferedImage, 0, 0, largura, altura, null);
					g.dispose();
					//tudo isso que fizemos acima esta em memória
					
					//agora aqui em baixo vamos fazer a saida dela, vamos fazer um array de saida ByteArrayOutputStream
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					//agora vamos obter o valor da imagem miniatura, resizedImage = é a imagem escrita em miniatura já tipo = png padrão e vamos escrever nesse objeto baos = new ByteArrayOutputStream
					ImageIO.write(resizedImage, "png", baos);
					
					//agora pegamos a parte certa da estrutura da imagem e concatenamos, pegamos os bytes do baos que esta em miniatura e convertemos em uma base 64 DatatypeConverter.printBase64Binary(baos.toByteArray())
					//agora ja temos a miniatura
					String miniImgBase64 = "data:image/png;base64," + DatatypeConverter.printBase64Binary(baos.toByteArray());
					
					//pega a imagem na posição x e passmos a variavel miniImgBase64
					produto.getImagens().get(x).setImagemMiniatura(miniImgBase64);
					
					//descarregar esses objetos
					bufferedImage.flush();
					resizedImage.flush();
					baos.flush();
					baos.close();
					
				}
			}
		}
		
		
		Produto produtoSalvo = produtoRepository.save(produto);
		
		
		
		if (produto.getAlertaQtdeEstoque() && produto.getQtdEstoque() <= 1) {
			
			StringBuilder html = new StringBuilder();
			html.append("<h2>")
			.append("Produto: " + produto.getNome())
			.append(" com estoque baixo: " + produto.getQtdEstoque());
			html.append("<p> Id Prod.:").append(produto.getId()).append("</p>");
			
			if (produto.getEmpresa().getEmail() != null) {
				serviceSendEmail.enviarEmailHtml("Produto sem estoque" , html.toString(), produto.getEmpresa().getEmail());
			}
		}
		
		return new ResponseEntity<Produto>(produtoSalvo, HttpStatus.OK);
	}
	
	
	
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/deleteProduto") /*Mapeando a url para receber JSON*/
	public ResponseEntity<String> deleteProduto(@RequestBody Produto produto) { /*Recebe o JSON e converte pra Objeto*/
		
		produtoRepository.deleteById(produto.getId());
		
		return new ResponseEntity<String>("Produto Removido",HttpStatus.OK);
	}
	

	//@Secured({ "ROLE_GERENTE", "ROLE_ADMIN" })
	@ResponseBody
	@DeleteMapping(value = "**/deleteProdutoPorId/{id}")
	public ResponseEntity<String> deleteProdutoPorId(@PathVariable("id") Long id) { 
		
		produtoRepository.deleteById(id);
		
		return new ResponseEntity<String>("Produto Removido",HttpStatus.OK);
	}
	
	
	
	@ResponseBody
	@GetMapping(value = "**/obterProduto/{id}")
	public ResponseEntity<Produto> obterAcesso(@PathVariable("id") Long id) throws ExceptionMentoriaJava { 
		
		Produto produto = produtoRepository.findById(id).orElse(null);
		
		if (produto == null) {
			throw new ExceptionMentoriaJava("Não encontrou Produto com código: " + id);
		}
		
		return new ResponseEntity<Produto>(produto,HttpStatus.OK);
	}
	
	
	
	@ResponseBody
	@GetMapping(value = "**/buscarProdNome/{desc}")
	public ResponseEntity<List<Produto>> buscarProdNome(@PathVariable("desc") String desc) { 
		
		List<Produto> acesso = produtoRepository.buscarProdutoNome(desc.toUpperCase());
		
		return new ResponseEntity<List<Produto>>(acesso,HttpStatus.OK);
	}

}
