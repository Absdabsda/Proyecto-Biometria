<?php
declare(strict_types=1);

final class Logica {
    private PDO $bd;
    private ?int $tipoPermitido;

    /** -------------------
 * bd:Persistencia, tipoPermitido:N|Ausente --> __construct() --> Logica
     * Pre: conexión configurada en UTC. Post: conserva dependencias. Errores: tipos.
 * ------------------- */
    public function __construct(PDO $bd, ?int $tipoPermitido = null) {
        $this->bd = $bd;
        $this->tipoPermitido = $tipoPermitido;
    }

    /** -------------------
 * entrada:MedidaEntrada, tipoPermitido:N|null --> validarMedida() --> entrada:MedidaEntrada
     * Pre: datos JSON decodificados. Post: campos exactos y rangos válidos.
     * Errores: InvalidArgumentException.
 * ------------------- */
    public static function validarMedida(array $entrada, ?int $tipoPermitido = null): array {
        $claves = array_keys($entrada);
        sort($claves);
        if ($claves !== ['tipo', 'valor']) {
            throw new InvalidArgumentException('Se requieren exactamente tipo y valor.');
        }
        foreach (['tipo' => [0, 255], 'valor' => [-32768, 32767]] as $campo => $rango) {
            if (!is_int($entrada[$campo]) || $entrada[$campo] < $rango[0] || $entrada[$campo] > $rango[1]) {
                throw new InvalidArgumentException('Campo fuera de rango o no entero: ' . $campo);
            }
        }
        if ($tipoPermitido !== null && $entrada['tipo'] !== $tipoPermitido) {
            throw new InvalidArgumentException('Código de gas no permitido.');
        }
        return $entrada;
    }

    /** -------------------
 * entrada:MedidaEntrada --> guardarMedida() --> medida:Medida
     * Pre: conexión disponible. Post: guarda una fila y devuelve su ID y fecha.
     * Errores: validación o PDOException.
 * ------------------- */
    public function guardarMedida(array $entrada): array {
        $entrada = self::validarMedida($entrada, $this->tipoPermitido);
        $consulta = $this->bd->prepare('INSERT INTO medidas (tipo, valor) VALUES (:tipo, :valor)');
        $consulta->execute($entrada);
        $id = $this->bd->lastInsertId();
        $consulta = $this->bd->prepare('SELECT medidaID, tipo, valor, fecha FROM medidas WHERE medidaID = ?');
        $consulta->execute([$id]);
        $fila = $consulta->fetch(PDO::FETCH_ASSOC);
        if (!$fila) { throw new RuntimeException('No se recuperó la fila insertada.'); }
        return self::serializar($fila);
    }

    /** -------------------
 * limit:N --> listarMedidas() --> medidas:[Medida]
     * Pre: 1 <= limit <= 100. Post: orden fecha e ID descendente.
     * Errores: validación o PDOException.
 * ------------------- */
    public function listarMedidas(int $limit = 50): array {
        if ($limit < 1 || $limit > 100) { throw new InvalidArgumentException('El límite debe estar entre 1 y 100.'); }
        $consulta = $this->bd->prepare('SELECT medidaID, tipo, valor, fecha FROM medidas ORDER BY fecha DESC, medidaID DESC LIMIT ?');
        $consulta->bindValue(1, $limit, PDO::PARAM_INT);
        $consulta->execute();
        return array_map([self::class, 'serializar'], $consulta->fetchAll(PDO::FETCH_ASSOC));
    }

    /** -------------------
 * fila:RegistroSQL --> serializar() --> medida:Medida
     * Pre: fecha recibida en UTC. Post: fecha ISO UTC, números con tipo estable.
     * Errores: fecha inválida.
 * ------------------- */
    private static function serializar(array $fila): array {
        // ID como texto evita perder precisión de BIGINT en JavaScript.
        $fila['medidaID'] = (string) $fila['medidaID'];
        foreach (['tipo', 'valor'] as $campo) { $fila[$campo] = (int) $fila[$campo]; }
        $fila['fecha'] = (new DateTimeImmutable($fila['fecha'], new DateTimeZone('UTC')))->format('Y-m-d\TH:i:s.v\Z');
        return $fila;
    }
}
