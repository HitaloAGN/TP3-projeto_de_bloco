import io.javalin.Javalin;
import io.javalin.json.JsonMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class AppMain {
    public static void main(String[] args) throws Exception {
        PaymentRepository repo = new PaymentRepository("pagamentos.csv");
        repo.ensureFileAndHeader();
        PaymentService service = new PaymentService(repo);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JsonMapper() {
                @NotNull
                @Override
                public String toJsonString(@NotNull Object obj, @NotNull Type type) {
                    return gson.toJson(obj, type);
                }

                @NotNull
                @Override
                public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
                    return gson.fromJson(json, targetType);
                }
            });
        }).start(7000);

        app.get("/", ctx -> {
            String html = """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Payments API</title>
                    <style>
                        * { box-sizing: border-box; }
                        body { 
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                            margin: 0; 
                            padding: 20px;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            min-height: 100vh;
                        }
                        .container { 
                            background: white; 
                            padding: 30px; 
                            border-radius: 12px; 
                            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
                            max-width: 1000px;
                            margin: 0 auto;
                        }
                        h1 { 
                            color: #333; 
                            margin-bottom: 10px;
                            font-size: 2.2em;
                        }
                        .status { 
                            color: #28a745; 
                            font-weight: bold;
                            font-size: 1.1em;
                        }
                        .endpoint { 
                            background: #f8f9fa; 
                            padding: 15px; 
                            margin: 12px 0; 
                            border-radius: 8px; 
                            border-left: 4px solid #007bff;
                            transition: transform 0.2s ease;
                        }
                        .endpoint:hover {
                            transform: translateX(5px);
                        }
                        .method { 
                            font-weight: bold; 
                            color: #007bff;
                            display: inline-block;
                            min-width: 60px;
                        }
                        .method.post { color: #28a745; }
                        .method.get { color: #007bff; }
                        .test-section { 
                            margin-top: 30px; 
                            padding: 25px; 
                            background: linear-gradient(135deg, #e9ecef 0%, #f8f9fa 100%); 
                            border-radius: 10px;
                        }
                        .btn { 
                            background: linear-gradient(135deg, #007bff, #0056b3);
                            color: white; 
                            padding: 12px 20px; 
                            border: none; 
                            border-radius: 6px; 
                            cursor: pointer; 
                            margin: 8px 5px; 
                            font-weight: 500;
                            transition: all 0.3s ease;
                            box-shadow: 0 2px 10px rgba(0,123,255,0.3);
                        }
                        .btn:hover { 
                            transform: translateY(-2px);
                            box-shadow: 0 4px 15px rgba(0,123,255,0.4);
                        }
                        .btn:active {
                            transform: translateY(0px);
                        }
                        .btn.success { background: linear-gradient(135deg, #28a745, #20c997); }
                        .btn.warning { background: linear-gradient(135deg, #ffc107, #fd7e14); }
                        .btn.danger { background: linear-gradient(135deg, #dc3545, #e83e8c); }
                        #result { 
                            margin-top: 20px; 
                            padding: 20px; 
                            background: white; 
                            border: 1px solid #ddd; 
                            border-radius: 8px; 
                            min-height: 120px;
                            font-family: 'Courier New', monospace;
                            white-space: pre-wrap;
                            box-shadow: inset 0 2px 5px rgba(0,0,0,0.1);
                        }
                        .loading {
                            color: #007bff;
                            font-style: italic;
                        }
                        .success { color: #28a745; }
                        .error { color: #dc3545; }
                        code {
                            background: #e9ecef;
                            padding: 2px 6px;
                            border-radius: 4px;
                            font-family: 'Courier New', monospace;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>Payments API - Sistema de Pagamentos</h1>
                        <p>API REST para gerenciamento de pagamentos. Status: <span class="status">ONLINE</span></p>
                        
                        <h2>Endpoints Disponíveis:</h2>
                        
                        <div class="endpoint">
                            <span class="method post">POST</span> <code>/payments</code> - Criar novo pagamento<br>
                            <small><strong>Body:</strong> {"clienteId": 1, "valorTotal": 100.50}</small>
                        </div>
                        
                        <div class="endpoint">
                            <span class="method get">GET</span> <code>/payments</code> - Listar todos os pagamentos
                        </div>
                        
                        <div class="endpoint">
                            <span class="method get">GET</span> <code>/payments/pending</code> - Obter primeiro pagamento pendente
                        </div>
                        
                        <div class="endpoint">
                            <span class="method post">POST</span> <code>/payments/confirm</code> - Confirmar primeiro pagamento pendente
                        </div>
                        
                        <div class="endpoint">
                            <span class="method get">GET</span> <code>/reports</code> - Relatório de pagamentos
                        </div>
                        
                        <div class="test-section">
                            <h3>🧪 Testar API:</h3>
                            <div style="margin-bottom: 15px;">
                                <button class="btn" onclick="testAPI('/payments', 'GET')">Listar Pagamentos</button>
                                <button class="btn" onclick="testAPI('/payments/pending', 'GET')">⏳ Ver Pendentes</button>
                                <button class="btn" onclick="testAPI('/reports', 'GET')">Relatório</button>
                            </div>
                            <div>
                                <button class="btn success" onclick="createPayment()">Criar Pagamento</button>
                                <button class="btn warning" onclick="testAPI('/payments/confirm', 'POST')">✅ Confirmar Pagamento</button>
                            </div>
                            
                            <div id="result">Clique em um botão para testar a API...</div>
                        </div>
                    </div>
                    
                    <script>
                        async function testAPI(endpoint, method = 'GET', body = null) {
                            const resultDiv = document.getElementById('result');
                            resultDiv.innerHTML = 'Carregando...';
                            resultDiv.className = 'loading';
                            
                            try {
                                const options = {
                                    method: method,
                                    headers: { 
                                        'Content-Type': 'application/json',
                                        'Accept': 'application/json'
                                    }
                                };
                                
                                if (body) {
                                    options.body = JSON.stringify(body);
                                }
                                
                                const response = await fetch(endpoint, options);
                                
                                let data;
                                let displayText = '';
                                
                                if (response.status === 204) {
                                    data = 'Nenhum conteúdo (204 - No Content)';
                                    displayText = data;
                                } else {
                                    const contentType = response.headers.get('content-type');
                                    if (contentType && contentType.includes('application/json')) {
                                        data = await response.json();
                                        displayText = JSON.stringify(data, null, 2);
                                    } else {
                                        data = await response.text();
                                        displayText = data;
                                    }
                                }
                                
                                const statusClass = response.ok ? 'success' : 'error';
                                resultDiv.className = statusClass;
                                resultDiv.innerHTML = `${method} ${endpoint}
📊 Status: ${response.status} ${response.statusText}
📄 Resposta:
${displayText}`;
                                
                            } catch (error) {
                                resultDiv.className = 'error';
                                resultDiv.innerHTML = `Erro de conexão: ${error.message}`;
                            }
                        }
                        
                        function createPayment() {
                            const clienteId = Math.floor(Math.random() * 100) + 1;
                            const valorTotal = Math.round((Math.random() * 1000 + 10) * 100) / 100;
                            testAPI('/payments', 'POST', { 
                                clienteId: clienteId, 
                                valorTotal: valorTotal 
                            });
                        }
                        
                        // Atualizar página a cada 30 segundos para verificar status
                        setInterval(() => {
                            console.log('API Health Check...');
                        }, 30000);
                    </script>
                </body>
                </html>
                """;
            ctx.html(html);
        });

        app.post("/payments", ctx -> {
            try {
                PaymentRequest req = ctx.bodyAsClass(PaymentRequest.class);
                if (req == null || req.getClienteId() == null || req.getValorTotal() == null) {
                    ctx.status(400).json(new ErrorResponse("Payload inválido. Envie clienteId e valorTotal."));
                    return;
                }

                Pagamento created = service.createPayment(req.getClienteId(), req.getValorTotal());
                ctx.status(201).json(created);
            } catch (IllegalStateException ex) {
                ctx.status(409).json(new ErrorResponse(ex.getMessage()));
            } catch (Exception ex) {
                ctx.status(500).json(new ErrorResponse("Erro interno: " + ex.getMessage()));
            }
        });

        app.get("/payments", ctx -> {
            try {
                List<Pagamento> all = repo.readAll();
                ctx.json(all);
            } catch (Exception e) {
                ctx.status(500).json(new ErrorResponse("Erro ao ler pagamentos: " + e.getMessage()));
            }
        });

        app.get("/payments/pending", ctx -> {
            try {
                Optional<Pagamento> pend = repo.firstPending();
                if (pend.isPresent()) {
                    ctx.json(pend.get());
                } else {
                    ctx.status(204);
                }
            } catch (Exception e) {
                ctx.status(500).json(new ErrorResponse("Erro: " + e.getMessage()));
            }
        });

        app.post("/payments/confirm", ctx -> {
            try {
                Optional<Pagamento> processed = service.processFirstPending();
                if (processed.isPresent()) {
                    ctx.json(processed.get()); // MUDANÇA: extrair o valor do Optional
                } else {
                    ctx.status(404).json(new ErrorResponse("Nenhum pagamento pendente encontrado."));
                }
            } catch (Exception e) {
                ctx.status(500).json(new ErrorResponse("Erro ao processar: " + e.getMessage()));
            }
        });

        app.get("/reports", ctx -> {
            try {
                ReportResponse r = service.generateReport();
                ctx.json(r);
            } catch (Exception e) {
                ctx.status(500).json(new ErrorResponse("Erro:" + e.getMessage()));
            }
        });
    }

    private record ErrorResponse(String error) {
    }
}