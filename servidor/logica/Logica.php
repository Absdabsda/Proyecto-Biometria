<?php
declare(strict_types=1);

final class Logica {
    private PDO $bd;
    private ?int $codigoO3;

    /** bd:PDO, codigoO3:N|null --> __construct() --> Logica
     * Pre: conexión configurada en UTC. Post: conserva dependencias. Errores: tipos. */
    public function __construct(PDO $bd, ?int $codigoO3 = null) {
        $this->bd = $bd;
        $this->codigoO3 = $codigoO3;
    }

    /** entrada:MedidaEntrada, codigoO3:N|null --> validarMedida() --> entrada:MedidaEntrada
     * Pre: datos JSON decodificados. Post: campos exactos y rangos válidos.
     * Errores: InvalidArgumentException. */
    public static function validarMedida(array $entrada, ?int $codigoO3 = null): array {
        $claves = array_keys($entrada);
        sort($claves);
        if ($claves !== ['contador', 'gas', 'uuid', 'valor']) {
            throw new InvalidArgumentException('Se requieren exactamente uuid, gas, valor y contador.');
        }
        if ($entrada['uuid'] !== 'EPSG-GTI-PROY-3A') {
            throw new InvalidArgumentException('UUID no válido.');
        }
        foreach (['gas' => [0, 255], 'valor' => [-32768, 32767], 'contador' => [0, 255]] as $campo => $rango) {
            if (!is_int($entrada[$campo]) || $entrada[$campo] < $rango[0] || $entrada[$campo] > $rango[1]) {
                throw new InvalidArgumentException('Campo fuera de rango o no entero: ' . $campo);
            }
        }
        if ($codigoO3 !== null && $entrada['gas'] !== $codigoO3) {
            throw new InvalidArgumentException('Código de gas no permitido.');
        }
        return $entrada;
    }

    /** entrada:MedidaEntrada --> guardarMedida() --> medida:Medida
     * Pre: conexión disponible. Post: guarda una fila y devuelve su ID y fecha.
     * Errores: validación o PDOException. */
    public function guardarMedida(array $entrada): array {
        $entrada = self::validarMedida($entrada, $this->codigoO3);
        $consulta = $this->bd->prepare('INSERT INTO medidas (uuid, gas, valor, contador) VALUES (:uuid, :gas, :valor, :contador)');
        $consulta->execute($entrada);
        $id = $this->bd->lastInsertId();
        $consulta = $this->bd->prepare('SELECT medidaID, uuid, gas, valor, contador, fecha FROM medidas WHERE medidaID = ?');
        $consulta->execute([$id]);
        $fila = $consulta->fetch(PDO::FETCH_ASSOC);
        if (!$fila) { throw new RuntimeException('No se recuperó la fila insertada.'); }
        return self::serializar($fila);
    }

    /** limit:N --> listarMedidas() --> medidas:[Medida]
     * Pre: 1 <= limit <= 100. Post: orden fecha e ID descendente.
     * Errores: validación o PDOException. */
    public function listarMedidas(int $limit = 50): array {
        if ($limit < 1 || $limit > 100) { throw new InvalidArgumentException('El límite debe estar entre 1 y 100.'); }
        $consulta = $this->bd->prepare('SELECT medidaID, uuid, gas, valor, contador, fecha FROM medidas ORDER BY fecha DESC, medidaID DESC LIMIT ?');
        $consulta->bindValue(1, $limit, PDO::PARAM_INT);
        $consulta->execute();
        return array_map([self::class, 'serializar'], $consulta->fetchAll(PDO::FETCH_ASSOC));
    }

    /** fila:RegistroSQL --> serializar() --> medida:Medida
     * Pre: fecha recibida en UTC. Post: fecha ISO UTC, números con tipo estable.
     * Errores: fecha inválida. */
    private static function serializar(array $fila): array {
        // ID como texto evita perder precisión de BIGINT en JavaScript.
        $fila['medidaID'] = (string) $fila['medidaID'];
        foreach (['gas', 'valor', 'contador'] as $campo) { $fila[$campo] = (int) $fila[$campo]; }
        $fila['fecha'] = (new DateTimeImmutable($fila['fecha'], new DateTimeZone('UTC')))->format('Y-m-d\TH:i:s.v\Z');
        return $fila;
    }
}
